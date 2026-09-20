package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NexusVoiceEngine(
    private val context: Context,
    private val onVoiceInputRecognized: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _audioRms = MutableStateFlow(0f)
    val audioRms = _audioRms.asStateFlow()

    private var pitchMultiplier = 0.85f // Deep, calm male timbre
    private var speechRateMultiplier = 1.0f

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    applyVoiceParameters()
                }

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    private fun applyVoiceParameters() {
        tts?.setPitch(pitchMultiplier)
        tts?.setSpeechRate(speechRateMultiplier)
    }

    fun updateVoiceConfig(pitch: Float, rate: Float, langCode: String) {
        this.pitchMultiplier = pitch
        this.speechRateMultiplier = rate
        applyVoiceParameters()

        val locale = when (langCode.lowercase()) {
            "hi" -> Locale("hi", "IN")
            "gu" -> Locale("gu", "IN")
            else -> Locale.US
        }
        tts?.language = locale
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize RMS (typically -2dB to 10dB) to 0f..1f for orb animation
                        val norm = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _audioRms.value = norm
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _audioRms.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _audioRms.value = 0f
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _audioRms.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val spokenText = matches[0]
                            onVoiceInputRecognized(spokenText)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            // Can show live preview
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening(langCode: String = "en-US") {
        stopSpeaking()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
        _isListening.value = true
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        _audioRms.value = 0f
    }

    fun speak(text: String, utteranceId: String = "nexus_voice_${System.currentTimeMillis()}") {
        if (!isTtsReady || text.isBlank()) return
        stopListening()
        _isSpeaking.value = true

        // Multilingual speech detection for Gujarati / Hindi text
        val hasGujarati = text.any { it in '\u0A80'..'\u0AFF' }
        val hasHindi = text.any { it in '\u0900'..'\u097F' }

        if (hasGujarati) {
            tts?.language = Locale("gu", "IN")
        } else if (hasHindi) {
            tts?.language = Locale("hi", "IN")
        } else {
            tts?.language = Locale.US
        }

        applyVoiceParameters()

        // Strip markdown asterisks and code fences for clear speech
        val cleanedText = text
            .replace(Regex("```[a-zA-Z]*"), "")
            .replace("```", "")
            .replace(Regex("[#*`>]"), "")
            .trim()

        tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
