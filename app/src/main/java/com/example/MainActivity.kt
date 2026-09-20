package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.NexusQuickDock
import com.example.ui.screens.AgentScreen
import com.example.ui.screens.AutomationsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InitializationScreen
import com.example.ui.screens.InterfacesScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VoiceScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NexusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val pendingConfirmation by viewModel.pendingRiskConfirmation.collectAsState()

                // Audio permission launcher
                val audioPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted && currentScreen == NexusScreen.VOICE) {
                        viewModel.startVoiceListening()
                    }
                }

                LaunchedEffect(currentScreen) {
                    if (currentScreen == NexusScreen.VOICE) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasPermission) {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                }

                val isQuickDockEnabled by viewModel.isQuickDockEnabled.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isQuickDockEnabled && currentScreen != NexusScreen.INITIALIZATION) {
                            NexusQuickDock(
                                currentScreen = currentScreen,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "NexusScreenTransition",
                        modifier = Modifier.padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            NexusScreen.INITIALIZATION -> InitializationScreen(viewModel)
                            NexusScreen.DASHBOARD -> DashboardScreen(viewModel)
                            NexusScreen.CHAT -> ChatScreen(viewModel)
                            NexusScreen.VOICE -> VoiceScreen(viewModel)
                            NexusScreen.AGENT -> AgentScreen(viewModel)
                            NexusScreen.PROJECTS -> ProjectsScreen(viewModel)
                            NexusScreen.AUTOMATIONS -> AutomationsScreen(viewModel)
                            NexusScreen.MEMORY -> MemoryScreen(viewModel)
                            NexusScreen.TOOLS -> ToolsScreen(viewModel)
                            NexusScreen.SETTINGS -> SettingsScreen(viewModel)
                            NexusScreen.FILES -> ProjectsScreen(viewModel)
                            NexusScreen.INTERFACES -> InterfacesScreen(viewModel)
                        }
                    }

                    // Security Clearance Modal
                    pendingConfirmation?.let { (msg, _) ->
                        ConfirmationDialog(
                            message = msg,
                            onConfirm = { viewModel.confirmPendingAction() },
                            onDismiss = { viewModel.dismissPendingAction() }
                        )
                    }
                }
            }
        }
    }
}

