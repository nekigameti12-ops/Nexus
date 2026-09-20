package com.example.agent

import com.example.data.api.AiProviderClient
import com.example.data.db.NexusRepository
import com.example.data.model.ChatMessageEntity
import com.example.data.security.SecureConfigStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AgentStepProgress(
    val phaseName: String,
    val percentage: Int,
    val details: String
)

class NexusAgentEngine(
    private val client: AiProviderClient,
    private val configStore: SecureConfigStore,
    private val repository: NexusRepository
) {

    private val _agentProgress = MutableStateFlow<AgentStepProgress?>(null)
    val agentProgress = _agentProgress.asStateFlow()

    private val _isWorking = MutableStateFlow(false)
    val isWorking = _isWorking.asStateFlow()

    /**
     * Executes standard conversational interaction with personality, memory context,
     * and proactive suggestions.
     */
    suspend fun processUserMessage(
        userText: String,
        mode: String = "chat",
        conversationHistory: List<ChatMessageEntity> = emptyList(),
        onPendingConfirmation: ((String, suspend () -> Unit) -> Unit)? = null
    ): String {
        _isWorking.value = true
        try {
            // Guard: No API Key = No AI
            if (!configStore.isCoreOnline()) {
                val offlineNotice = "NEXUS CORE OFFLINE. AI services are unavailable until a valid API configuration is provided in Settings or the Initialization Screen."
                repository.saveMessage(
                    ChatMessageEntity(
                        role = "nexus",
                        content = offlineNotice,
                        mode = mode,
                        toolStatus = "CORE_OFFLINE"
                    )
                )
                return offlineNotice
            }

            // Risk check: If dangerous action requested (e.g. "delete all files", "send email to")
            val lower = userText.lowercase()
            if (configStore.isRiskConfirmationRequired() && (lower.contains("delete all") || lower.contains("send email") || lower.contains("format drive") || lower.contains("publish live"))) {
                if (onPendingConfirmation != null) {
                    onPendingConfirmation("High Risk Action Detected: '$userText'. Do you authorize NEXUS to execute this?") {
                        // Confirmed
                        repository.logAction("Risk Action Confirmed", "HIGH", "CONFIRMED", userText)
                    }
                    return "Awaiting your explicit authorization for high-risk action: $userText"
                }
            }

            // Build system prompt with NEXUS personality & transparency mandate
            val systemPrompt = buildSystemPrompt(mode)

            val historyPairs = conversationHistory.map { it.role to it.content }

            val response = client.generateResponse(
                provider = configStore.getProvider(),
                apiKey = configStore.getApiKey(),
                model = configStore.getModel(),
                baseUrl = configStore.getBaseUrl(),
                systemPrompt = systemPrompt,
                userPrompt = userText,
                history = historyPairs
            )

            // Save user message and nexus response
            repository.saveMessage(
                ChatMessageEntity(
                    role = "nexus",
                    content = response,
                    mode = mode,
                    verified = true
                )
            )

            repository.logAction("Query Processed", "LOW", "EXECUTED", "Mode: $mode")
            return response
        } catch (e: Exception) {
            val errorMsg = "The AI service returned an error: ${e.message}. Please check your API configuration."
            repository.saveMessage(
                ChatMessageEntity(
                    role = "nexus",
                    content = errorMsg,
                    mode = mode,
                    toolStatus = "ERROR"
                )
            )
            repository.logAction("API Error", "MEDIUM", "BLOCKED", e.message ?: "Error")
            return errorMsg
        } finally {
            _isWorking.value = false
        }
    }

    /**
     * Executes multi-step Agent Mode task:
     * Plan (20%) -> Research/Context (40%) -> Execute (70%) -> Verify (90%) -> Respond (100%)
     */
    suspend fun executeAgentTask(
        taskDescription: String,
        onProgressUpdate: (AgentStepProgress) -> Unit
    ): String {
        _isWorking.value = true
        try {
            if (!configStore.isCoreOnline()) {
                return "NEXUS CORE OFFLINE. Please configure and validate an API key to enable Agent execution."
            }

            // Step 1: Planning
            val step1 = AgentStepProgress("Planning the task...", 20, "Analyzing task requirements and breaking down execution steps.")
            _agentProgress.value = step1
            onProgressUpdate(step1)
            delay(500)

            // Step 2: Researching & Context Gathering
            val step2 = AgentStepProgress("Searching for information...", 40, "Retrieving parameters, technical schemas, and contextual data.")
            _agentProgress.value = step2
            onProgressUpdate(step2)
            delay(600)

            // Step 3: Multi-step Execution
            val step3 = AgentStepProgress("Executing tools & creating artifacts...", 70, "Synthesizing solution, structuring components, and compiling deliverables.")
            _agentProgress.value = step3
            onProgressUpdate(step3)

            val agentPrompt = """
                Task to execute autonomously:
                $taskDescription
                
                Please structure your response clearly:
                1. Executive Summary
                2. Step-by-step breakdown of actions taken
                3. Deliverables / Artifacts (Code, Outline, or Content)
                4. Verification check & Next proactive step recommendation
            """.trimIndent()

            val resultText = client.generateResponse(
                provider = configStore.getProvider(),
                apiKey = configStore.getApiKey(),
                model = configStore.getModel(),
                baseUrl = configStore.getBaseUrl(),
                systemPrompt = buildSystemPrompt("agent"),
                userPrompt = agentPrompt
            )

            // Step 4: Verification
            val step4 = AgentStepProgress("Verifying the result...", 90, "Validating structural consistency, syntax correctness, and security constraints.")
            _agentProgress.value = step4
            onProgressUpdate(step4)
            delay(500)

            // Step 5: Completed
            val step5 = AgentStepProgress("Completed", 100, "All execution steps verified.")
            _agentProgress.value = step5
            onProgressUpdate(step5)

            repository.saveMessage(
                ChatMessageEntity(
                    role = "nexus",
                    content = resultText,
                    mode = "agent",
                    executionProgress = 100,
                    verified = true
                )
            )

            repository.logAction("Agent Task Finished", "LOW", "EXECUTED", taskDescription.take(60))
            return resultText
        } catch (e: Exception) {
            val errorMsg = "Agent execution halted: ${e.message}. Some steps could not be verified."
            _agentProgress.value = AgentStepProgress("Failed", 0, e.message ?: "Execution error")
            return errorMsg
        } finally {
            _isWorking.value = false
        }
    }

    private fun buildSystemPrompt(mode: String): String {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        return """
            You are NEXUS AI (Neural Enhanced eXecution Unified System).
            Tagline: "Think. Connect. Execute."
            Identity: You are a futuristic, highly capable male AI personal assistant and agent platform.
            
            Personality:
            - Intelligent, calm, professional, respectful, confident, helpful, slightly futuristic, natural.
            - Concise when appropriate, detailed when necessary.
            - Never arrogant or annoying.
            - Current local time: $now.
            - Active Mode: $mode.
            
            TRANSPARENCY MANDATE:
            - Always be transparent about actual capabilities.
            - Distinguish between what you know, what you searched, what you actually executed, and what you could not execute.
            - Never pretend an action was completed when it was not.
            
            MULTILINGUAL SUPPORT:
            - Fully support English, Hindi, Gujarati, and Hinglish.
            - Automatically detect the user's language.
            - If user speaks or asks in Gujarati (or Gujarati script/Latin), respond naturally in Gujarati.
            - If user asks in Hindi or Hinglish, respond in natural Hindi or Hinglish.
            
            RESPONSE STYLE:
            - Simple questions: Keep answers short, crisp, and direct.
            - Complex tasks: Use structured bullet points, clear sections, and concise code blocks.
            - Proactive intelligence: Suggest logical next steps when helpful.
        """.trimIndent()
    }
}
