package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.JarvisArcReactorOrb
import com.example.ui.components.NexusCoreOrb
import com.example.ui.components.NexusCoreState
import com.example.ui.theme.JarvisFire
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisOrange
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBlue
import com.example.ui.theme.NexusCrimson
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusVoid

@Composable
fun VoiceScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val coreState by viewModel.coreState.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val audioRms by viewModel.voiceEngine.audioRms.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()

    val lastMessage = messages.lastOrNull()
    val currentLanguage = viewModel.configStore.getLanguage()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFF0F1A30),
                        NexusVoid
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NEXUS VOICE TERMINAL",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = NexusCyan
                )
                Text(
                    text = "Live neural audio stream active",
                    fontSize = 11.sp,
                    color = NexusTextSecondary
                )
            }

            IconButton(
                onClick = {
                    viewModel.voiceEngine.stopListening()
                    viewModel.voiceEngine.stopSpeaking()
                    viewModel.navigateTo(NexusScreen.DASHBOARD)
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NexusSurface)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = NexusTextSecondary)
            }
        }

        // Center Area: Large Reactive Core & Live Waveform
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            JarvisArcReactorOrb(
                state = coreState,
                audioRms = audioRms,
                sizeDp = 270.dp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Dynamic Audio Waveform Bars (reacts to audioRms)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(40.dp)
            ) {
                val barCount = 18
                for (i in 0 until barCount) {
                    val factor = kotlin.math.sin(i.toDouble() / barCount * Math.PI).toFloat()
                    val targetHeight = if (isListening) {
                        (8f + (audioRms * 32f * factor)).coerceIn(6f, 36f)
                    } else if (isSpeaking) {
                        (10f + (22f * factor)).coerceIn(6f, 32f)
                    } else {
                        4f
                    }

                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(targetHeight.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isListening) NexusCyan
                                else if (isSpeaking) NexusBlue
                                else NexusTextDim.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Real-time transcript display box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NexusSurface.copy(alpha = 0.8f))
                    .border(1.dp, NexusBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isListening) "LISTENING TO USER..."
                               else if (isSpeaking) "NEXUS SPEAKING..."
                               else "TAP MIC TO SPEAK",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isListening) NexusCyan else if (isSpeaking) NexusBlue else NexusTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lastMessage?.content ?: "Ask NEXUS anything in English, Hindi, or Gujarati...",
                        fontSize = 14.sp,
                        color = NexusTextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        maxLines = 4
                    )
                }
            }
        }

        // Bottom Controls Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NexusSurface)
                        .clickable {
                            val nextLang = when (currentLanguage) {
                                "en" -> "hi"
                                "hi" -> "gu"
                                else -> "en"
                            }
                            viewModel.updateVoiceSettings(
                                pitch = viewModel.configStore.getVoicePitch(),
                                rate = viewModel.configStore.getVoiceRate(),
                                lang = nextLang
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = "Language", tint = NexusCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                "hi" -> "HINDI"
                                "gu" -> "GUJARATI"
                                else -> "ENGLISH"
                            },
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NexusCyan
                        )
                    }
                }

                // Primary Large Microphone Toggle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) NexusCrimson else NexusCyan
                        )
                        .clickable {
                            if (isListening) {
                                viewModel.stopVoiceListening()
                            } else {
                                viewModel.startVoiceListening()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Toggle Mic",
                        tint = NexusVoid,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Interrupt / Stop Speaking Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NexusSurface)
                        .clickable {
                            viewModel.voiceEngine.stopSpeaking()
                            viewModel.voiceEngine.stopListening()
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VolumeOff, contentDescription = "Silence", tint = NexusTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SILENCE",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NexusTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Natural Male Voice Engine Active",
                fontSize = 11.sp,
                color = NexusTextDim
            )
        }
    }
}
