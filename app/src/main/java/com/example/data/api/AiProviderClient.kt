package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiProviderClient {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Genuinely validates the configured API key with a ping prompt.
     * Throws an exception or returns false with error detail if the key is rejected.
     */
    suspend fun validateConnection(
        provider: String,
        apiKey: String,
        model: String,
        baseUrl: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("API Key cannot be blank."))
        }

        try {
            val pingResponse = generateResponse(
                provider = provider,
                apiKey = apiKey,
                model = model,
                baseUrl = baseUrl,
                systemPrompt = "You are a health check system. Reply with 'OK'.",
                userPrompt = "Ping"
            )
            if (pingResponse.isNotBlank()) {
                Result.success("Connection verified successfully: $model")
            } else {
                Result.failure(Exception("AI provider returned an empty response."))
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown connection error"
            Result.failure(Exception(msg))
        }
    }

    suspend fun generateResponse(
        provider: String,
        apiKey: String,
        model: String,
        baseUrl: String,
        systemPrompt: String,
        userPrompt: String,
        history: List<Pair<String, String>> = emptyList() // role to text
    ): String = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        val p = provider.trim().lowercase()

        when {
            p.contains("gemini") -> callGeminiRest(trimmedKey, model, systemPrompt, userPrompt, history)
            else -> callOpenAiCompatible(p, trimmedKey, model, baseUrl, systemPrompt, userPrompt, history)
        }
    }

    private fun callGeminiRest(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userPrompt: String,
        history: List<Pair<String, String>>
    ): String {
        val resolvedModel = if (model.isBlank()) "gemini-2.5-flash" else model.trim()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$resolvedModel:generateContent?key=$apiKey"

        val contentsArray = JSONArray()

        // Append conversation history
        for ((role, text) in history.takeLast(8)) {
            val contentObj = JSONObject()
            contentObj.put("role", if (role == "nexus" || role == "assistant") "model" else "user")
            val parts = JSONArray()
            parts.put(JSONObject().put("text", text))
            contentObj.put("parts", parts)
            contentsArray.put(contentObj)
        }

        // Current user prompt
        val currentObj = JSONObject()
        currentObj.put("role", "user")
        val currentParts = JSONArray()
        currentParts.put(JSONObject().put("text", userPrompt))
        currentObj.put("parts", currentParts)
        contentsArray.put(currentObj)

        val root = JSONObject()
        root.put("contents", contentsArray)

        if (systemPrompt.isNotBlank()) {
            val sysObj = JSONObject()
            val sysParts = JSONArray()
            sysParts.put(JSONObject().put("text", systemPrompt))
            sysObj.put("parts", sysParts)
            root.put("systemInstruction", sysObj)
        }

        val request = Request.Builder()
            .url(url)
            .post(root.toString().toRequestBody(jsonMediaType))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            val errorMsg = try {
                val errJson = JSONObject(responseBody).optJSONObject("error")
                errJson?.optString("message") ?: "HTTP ${response.code}: $responseBody"
            } catch (e: Exception) {
                "HTTP ${response.code}: $responseBody"
            }
            throw Exception(errorMsg)
        }

        try {
            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        sb.append(part.optString("text"))
                    }
                    return sb.toString()
                }
            }
            return "NEXUS received an empty response structure."
        } catch (e: Exception) {
            throw Exception("Failed to parse Gemini response: ${e.message}")
        }
    }

    private fun callOpenAiCompatible(
        provider: String,
        apiKey: String,
        model: String,
        baseUrl: String,
        systemPrompt: String,
        userPrompt: String,
        history: List<Pair<String, String>>
    ): String {
        val endpoint = when {
            baseUrl.isNotBlank() -> {
                var url = baseUrl.trimEnd('/')
                if (!url.endsWith("/chat/completions")) {
                    url += if (url.endsWith("/v1")) "/chat/completions" else "/v1/chat/completions"
                }
                url
            }
            provider.contains("groq") -> "https://api.groq.com/openai/v1/chat/completions"
            else -> "https://api.openai.com/v1/chat/completions"
        }

        val resolvedModel = when {
            model.isNotBlank() -> model.trim()
            provider.contains("groq") -> "llama-3.3-70b-versatile"
            else -> "gpt-4o"
        }

        val messagesArray = JSONArray()
        if (systemPrompt.isNotBlank()) {
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })
        }

        for ((role, text) in history.takeLast(8)) {
            messagesArray.put(JSONObject().apply {
                put("role", if (role == "nexus" || role == "assistant") "assistant" else "user")
                put("content", text)
            })
        }

        messagesArray.put(JSONObject().apply {
            put("role", "user")
            put("content", userPrompt)
        })

        val root = JSONObject().apply {
            put("model", resolvedModel)
            put("messages", messagesArray)
            put("temperature", 0.7)
        }

        val request = Request.Builder()
            .url(endpoint)
            .header("Authorization", "Bearer $apiKey")
            .post(root.toString().toRequestBody(jsonMediaType))
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            val errorMsg = try {
                val errJson = JSONObject(responseBody).optJSONObject("error")
                errJson?.optString("message") ?: "HTTP ${response.code}: $responseBody"
            } catch (e: Exception) {
                "HTTP ${response.code}: $responseBody"
            }
            throw Exception(errorMsg)
        }

        try {
            val json = JSONObject(responseBody)
            val choices = json.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val first = choices.getJSONObject(0)
                val msg = first.optJSONObject("message")
                return msg?.optString("content") ?: "No response generated."
            }
            return "No choices returned by AI provider."
        } catch (e: Exception) {
            throw Exception("Failed to parse provider response: ${e.message}")
        }
    }
}
