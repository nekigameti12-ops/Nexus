package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agent.AgentStepProgress
import com.example.agent.NexusAgentEngine
import com.example.data.api.AiProviderClient
import com.example.data.db.NexusDatabase
import com.example.data.db.NexusRepository
import com.example.data.model.AutomationEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MemoryItemEntity
import com.example.data.model.ProjectEntity
import com.example.data.security.SecureConfigStore
import com.example.ui.components.NexusCoreState
import com.example.voice.NexusVoiceEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NexusScreen {
    INITIALIZATION,
    DASHBOARD,
    CHAT,
    VOICE,
    AGENT,
    PROJECTS,
    FILES,
    AUTOMATIONS,
    MEMORY,
    TOOLS,
    SETTINGS,
    INTERFACES
}

sealed interface ValidationStatus {
    object Idle : ValidationStatus
    data class Validating(val message: String = "VALIDATING NEXUS CORE...") : ValidationStatus
    data class Success(val message: String = "NEXUS CORE INITIALIZED") : ValidationStatus
    data class Error(val message: String) : ValidationStatus
}

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    val configStore = SecureConfigStore(application)
    private val db = NexusDatabase.getInstance(application)
    val repository = NexusRepository(db.nexusDao())
    private val apiClient = AiProviderClient()
    val agentEngine = NexusAgentEngine(apiClient, configStore, repository)

    // Voice engine
    val voiceEngine = NexusVoiceEngine(application) { spokenText ->
        handleVoiceInput(spokenText)
    }

    private val _currentScreen = MutableStateFlow<NexusScreen>(
        if (configStore.isCoreOnline()) NexusScreen.DASHBOARD else NexusScreen.INITIALIZATION
    )
    val currentScreen = _currentScreen.asStateFlow()

    private val _coreState = MutableStateFlow<NexusCoreState>(
        if (configStore.isCoreOnline()) NexusCoreState.IDLE else NexusCoreState.OFFLINE
    )
    val coreState = _coreState.asStateFlow()

    private val _validationStatus = MutableStateFlow<ValidationStatus>(ValidationStatus.Idle)
    val validationStatus = _validationStatus.asStateFlow()

    private val _pendingRiskConfirmation = MutableStateFlow<Pair<String, () -> Unit>?>(null)
    val pendingRiskConfirmation = _pendingRiskConfirmation.asStateFlow()

    // Real-time observation of database
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.projects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationEntity>> = repository.automations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryItems: StateFlow<List<MemoryItemEntity>> = repository.memoryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agentProgress: StateFlow<AgentStepProgress?> = agentEngine.agentProgress

    private val _accentTheme = MutableStateFlow(configStore.getAccentTheme())
    val accentTheme = _accentTheme.asStateFlow()

    private val _uiDensity = MutableStateFlow(configStore.getUiDensity())
    val uiDensity = _uiDensity.asStateFlow()

    private val _isQuickDockEnabled = MutableStateFlow(configStore.isQuickDockEnabled())
    val isQuickDockEnabled = _isQuickDockEnabled.asStateFlow()

    fun setAccentTheme(theme: String) {
        configStore.setAccentTheme(theme)
        _accentTheme.value = theme
    }

    fun setUiDensity(density: String) {
        configStore.setUiDensity(density)
        _uiDensity.value = density
    }

    fun setQuickDockEnabled(enabled: Boolean) {
        configStore.setQuickDockEnabled(enabled)
        _isQuickDockEnabled.value = enabled
    }

    init {
        // Sync voice engine RMS and speaking state with Core State
        viewModelScope.launch {
            voiceEngine.isListening.collect { listening ->
                if (!configStore.isCoreOnline()) {
                    _coreState.value = NexusCoreState.OFFLINE
                } else if (listening) {
                    _coreState.value = NexusCoreState.LISTENING
                } else if (voiceEngine.isSpeaking.value) {
                    _coreState.value = NexusCoreState.SPEAKING
                } else if (agentEngine.isWorking.value) {
                    _coreState.value = NexusCoreState.THINKING
                } else {
                    _coreState.value = NexusCoreState.IDLE
                }
            }
        }

        viewModelScope.launch {
            voiceEngine.isSpeaking.collect { speaking ->
                if (!configStore.isCoreOnline()) {
                    _coreState.value = NexusCoreState.OFFLINE
                } else if (speaking) {
                    _coreState.value = NexusCoreState.SPEAKING
                } else if (agentEngine.isWorking.value) {
                    _coreState.value = NexusCoreState.THINKING
                } else {
                    _coreState.value = NexusCoreState.IDLE
                }
            }
        }

        viewModelScope.launch {
            agentEngine.isWorking.collect { working ->
                if (!configStore.isCoreOnline()) {
                    _coreState.value = NexusCoreState.OFFLINE
                } else if (working) {
                    _coreState.value = NexusCoreState.THINKING
                } else if (!voiceEngine.isSpeaking.value && !voiceEngine.isListening.value) {
                    _coreState.value = NexusCoreState.IDLE
                }
            }
        }

        // Seed initial project & automation samples if database is brand new
        viewModelScope.launch {
            checkAndSeedInitialData()
        }
    }

    private suspend fun checkAndSeedInitialData() {
        // Only seed if empty to preserve user state
        // Add sample default memory preference
        if (configStore.isCoreOnline()) {
            voiceEngine.speak("NEXUS online. How can I assist you?")
        }
    }

    fun navigateTo(screen: NexusScreen) {
        // Guard: If Core is offline, AI interaction screens require API configuration
        if (!configStore.isCoreOnline() && (screen == NexusScreen.CHAT || screen == NexusScreen.VOICE || screen == NexusScreen.AGENT)) {
            _currentScreen.value = NexusScreen.INITIALIZATION
            return
        }
        _currentScreen.value = screen
    }

    /**
     * Mandatory First-Launch / Settings API Validation
     */
    fun validateAndInitialize(
        provider: String,
        apiKey: String,
        model: String,
        baseUrl: String
    ) {
        viewModelScope.launch {
            _validationStatus.value = ValidationStatus.Validating("VALIDATING NEXUS CORE...")
            _coreState.value = NexusCoreState.THINKING

            val result = apiClient.validateConnection(
                provider = provider,
                apiKey = apiKey,
                model = model,
                baseUrl = baseUrl
            )

            result.onSuccess {
                configStore.saveConfig(provider, apiKey, model, baseUrl)
                configStore.setCoreOnline(true)
                _validationStatus.value = ValidationStatus.Success("NEXUS CORE INITIALIZED • NEXUS ONLINE")
                _coreState.value = NexusCoreState.IDLE
                repository.logAction("Core Initialization", "MEDIUM", "CONFIRMED", "Provider: $provider, Model: $model")

                // Vocal greeting
                voiceEngine.speak("NEXUS online. How can I assist you?")
                _currentScreen.value = NexusScreen.DASHBOARD
            }.onFailure { err ->
                configStore.setCoreOnline(false)
                val errMsg = err.message ?: "Authentication failed. Please verify your API configuration."
                _validationStatus.value = ValidationStatus.Error("API KEY INVALID: $errMsg")
                _coreState.value = NexusCoreState.OFFLINE
                repository.logAction("Validation Failed", "HIGH", "REJECTED", errMsg)
            }
        }
    }

    fun testConnection() {
        val key = configStore.getApiKey()
        if (key.isBlank()) {
            _validationStatus.value = ValidationStatus.Error("No API key configured.")
            return
        }
        validateAndInitialize(
            provider = configStore.getProvider(),
            apiKey = key,
            model = configStore.getModel(),
            baseUrl = configStore.getBaseUrl()
        )
    }

    fun removeApiKey() {
        configStore.removeApiKey()
        _coreState.value = NexusCoreState.OFFLINE
        _validationStatus.value = ValidationStatus.Idle
        _currentScreen.value = NexusScreen.INITIALIZATION
        viewModelScope.launch {
            repository.logAction("Credential Cleared", "HIGH", "EXECUTED", "AI functionality disconnected")
        }
    }

    fun sendChatMessage(text: String, mode: String = "chat") {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Save user bubble first
            repository.saveMessage(
                ChatMessageEntity(
                    role = "user",
                    content = text,
                    mode = mode
                )
            )

            // Process with Agent Engine
            val response = agentEngine.processUserMessage(
                userText = text,
                mode = mode,
                conversationHistory = chatMessages.value,
                onPendingConfirmation = { riskNotice, action ->
                    _pendingRiskConfirmation.value = riskNotice to {
                        viewModelScope.launch { action() }
                        _pendingRiskConfirmation.value = null
                    }
                }
            )

            // Voice response in voice mode
            if (mode == "voice" && configStore.isCoreOnline()) {
                voiceEngine.speak(response)
            }
        }
    }

    private fun handleVoiceInput(spokenText: String) {
        if (spokenText.isBlank()) return
        sendChatMessage(spokenText, mode = "voice")
    }

    fun startVoiceListening() {
        val lang = when (configStore.getLanguage()) {
            "hi" -> "hi-IN"
            "gu" -> "gu-IN"
            else -> "en-US"
        }
        voiceEngine.startListening(lang)
    }

    fun stopVoiceListening() {
        voiceEngine.stopListening()
    }

    fun runAgentTask(taskPrompt: String) {
        if (taskPrompt.isBlank()) return
        viewModelScope.launch {
            repository.saveMessage(
                ChatMessageEntity(
                    role = "user",
                    content = taskPrompt,
                    mode = "agent"
                )
            )
            _coreState.value = NexusCoreState.EXECUTING
            agentEngine.executeAgentTask(taskPrompt) { step ->
                // Progress callback
            }
            _coreState.value = NexusCoreState.IDLE
        }
    }

    fun confirmPendingAction() {
        _pendingRiskConfirmation.value?.second?.invoke()
        _pendingRiskConfirmation.value = null
    }

    fun dismissPendingAction() {
        _pendingRiskConfirmation.value = null
    }

    // Projects
    fun createProject(title: String, desc: String, category: String, requirements: String) {
        viewModelScope.launch {
            repository.saveProject(
                ProjectEntity(
                    title = title,
                    description = desc,
                    category = category,
                    requirements = requirements,
                    status = "Active"
                )
            )
            repository.logAction("Project Created", "LOW", "EXECUTED", title)
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            repository.logAction("Project Deleted", "MEDIUM", "EXECUTED", "ID: $id")
        }
    }

    // Automations
    fun createAutomation(title: String, trigger: String, condition: String, action: String, time: String) {
        viewModelScope.launch {
            repository.saveAutomation(
                AutomationEntity(
                    title = title,
                    triggerDesc = trigger,
                    conditionDesc = condition,
                    actionDesc = action,
                    scheduleTime = time,
                    isActive = true
                )
            )
            repository.logAction("Automation Scheduled", "LOW", "EXECUTED", title)
        }
    }

    fun toggleAutomation(automation: AutomationEntity) {
        viewModelScope.launch {
            repository.updateAutomation(automation.copy(isActive = !automation.isActive))
        }
    }

    fun deleteAutomation(id: Long) {
        viewModelScope.launch {
            repository.deleteAutomation(id)
        }
    }

    // Memory
    fun addMemoryItem(category: String, key: String, value: String) {
        viewModelScope.launch {
            repository.saveMemory(
                MemoryItemEntity(
                    category = category,
                    key = key,
                    value = value
                )
            )
            repository.logAction("Memory Stored", "LOW", "EXECUTED", "$category: $key")
        }
    }

    fun deleteMemoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearAllMemory() {
        viewModelScope.launch {
            repository.clearMemory()
            repository.logAction("Memory Purged", "MEDIUM", "EXECUTED", "Full purge by user")
        }
    }

    // Voice & System Configuration
    fun updateVoiceSettings(pitch: Float, rate: Float, lang: String) {
        configStore.setVoicePitch(pitch)
        configStore.setVoiceRate(rate)
        configStore.setLanguage(lang)
        voiceEngine.updateVoiceConfig(pitch, rate, lang)
    }

    fun togglePrivacyMode(enabled: Boolean) {
        configStore.setPrivacyMode(enabled)
        viewModelScope.launch {
            repository.logAction("Privacy Mode Toggled", "LOW", "EXECUTED", "Enabled: $enabled")
        }
    }

    fun toggleRiskConfirmation(required: Boolean) {
        configStore.setRiskConfirmationRequired(required)
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
            repository.logAction("Chat Cleared", "LOW", "EXECUTED", "Chat history erased")
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
