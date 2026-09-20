package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
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
fun SettingsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val config = viewModel.configStore
    val isOnline = config.isCoreOnline()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var voicePitch by remember { mutableFloatStateOf(config.getVoicePitch()) }
    var voiceRate by remember { mutableFloatStateOf(config.getVoiceRate()) }
    var language by remember { mutableStateOf(config.getLanguage()) }
    var privacyMode by remember { mutableStateOf(config.isPrivacyMode()) }
    var riskConfirmation by remember { mutableStateOf(config.isRiskConfirmationRequired()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NexusCyan)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "SETTINGS & CREDENTIAL VAULT",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NexusCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Security, AI Provider, and Male Voice",
                    fontSize = 11.sp,
                    color = NexusTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 0: Interface Showcase & Customizer
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.navigateTo(NexusScreen.INTERFACES) },
            borderColor = NexusCyan.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NexusCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.ViewQuilt, contentDescription = "Interfaces", tint = NexusCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "APP INTERFACES & THEME",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = NexusCyan
                        )
                        Text(
                            text = "ऐप के सभी 10 इंटरफेस देखें व थीम बदलें",
                            fontSize = 11.sp,
                            color = NexusTextSecondary
                        )
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(NexusScreen.INTERFACES) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusCyan)
                ) {
                    Text("OPEN / देखें", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section 1: AI Provider & Credential Management
        Text(
            text = "AI PROVIDER & CREDENTIALS",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NexusCyan,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (isOnline) NexusCyan.copy(alpha = 0.4f) else NexusCrimson.copy(alpha = 0.4f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = "Key", tint = NexusCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = config.getProvider().uppercase(),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NexusTextPrimary
                        )
                    }

                    Text(
                        text = if (isOnline) "ONLINE" else "DISCONNECTED",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) NexusEmerald else NexusCrimson
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Stored Key: ${config.getMaskedApiKey()}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NexusTextSecondary
                )
                Text(
                    text = "Model: ${config.getModel()}",
                    fontSize = 11.sp,
                    color = NexusTextDim
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.testConnection() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("TEST PING", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NexusCyan)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(NexusScreen.INITIALIZATION) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CHANGE KEY", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NexusBlue)
                    }

                    Button(
                        onClick = { viewModel.removeApiKey() },
                        colors = ButtonDefaults.buttonColors(containerColor = NexusCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("remove_api_key_button")
                    ) {
                        Text("REMOVE", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NexusVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Male Voice Configuration
        Text(
            text = "MALE VOICE ENGINE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NexusCyan,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = "Voice", tint = NexusBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voice Pitch: ${"%.2f".format(voicePitch)}x (Calm Male Timbre)",
                        fontSize = 12.sp,
                        color = NexusTextPrimary
                    )
                }

                Slider(
                    value = voicePitch,
                    onValueChange = {
                        voicePitch = it
                        viewModel.updateVoiceSettings(voicePitch, voiceRate, language)
                    },
                    valueRange = 0.70f..1.30f,
                    colors = SliderDefaults.colors(
                        thumbColor = NexusCyan,
                        activeTrackColor = NexusCyan,
                        inactiveTrackColor = NexusSurface
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Speech Rate: ${"%.2f".format(voiceRate)}x",
                    fontSize = 12.sp,
                    color = NexusTextPrimary
                )

                Slider(
                    value = voiceRate,
                    onValueChange = {
                        voiceRate = it
                        viewModel.updateVoiceSettings(voicePitch, voiceRate, language)
                    },
                    valueRange = 0.8f..1.4f,
                    colors = SliderDefaults.colors(
                        thumbColor = NexusBlue,
                        activeTrackColor = NexusBlue,
                        inactiveTrackColor = NexusSurface
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "PRIMARY LANGUAGE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NexusTextDim
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("en" to "English", "hi" to "Hindi", "gu" to "Gujarati").forEach { (code, label) ->
                        val isSelected = language == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NexusCyan.copy(alpha = 0.2f) else NexusSurface)
                                .border(1.dp, if (isSelected) NexusCyan else NexusTextDim.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable {
                                    language = code
                                    viewModel.updateVoiceSettings(voicePitch, voiceRate, language)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = label, fontSize = 11.sp, color = if (isSelected) NexusCyan else NexusTextSecondary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Security & Privacy
        Text(
            text = "SECURITY & RISK-BASED CLEARANCE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NexusCyan,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mandatory Risk Confirmation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexusTextPrimary
                        )
                        Text(
                            text = "Intercept high-risk actions (file deletion, emails, external API execution)",
                            fontSize = 11.sp,
                            color = NexusTextSecondary
                        )
                    }

                    Switch(
                        checked = riskConfirmation,
                        onCheckedChange = {
                            riskConfirmation = it
                            viewModel.toggleRiskConfirmation(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NexusEmerald, checkedTrackColor = NexusEmerald.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Strict Privacy Isolation Mode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexusTextPrimary
                        )
                        Text(
                            text = "Disables cloud indexing, retains zero session logs",
                            fontSize = 11.sp,
                            color = NexusTextSecondary
                        )
                    }

                    Switch(
                        checked = privacyMode,
                        onCheckedChange = {
                            privacyMode = it
                            viewModel.togglePrivacyMode(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = NexusCyan, checkedTrackColor = NexusCyan.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: Security Audit Trail
        Text(
            text = "SECURITY AUDIT LOG (LAST 50 EVENTS)",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NexusTextDim,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (auditLogs.isEmpty()) {
                    Text("No audit events recorded yet.", fontSize = 11.sp, color = NexusTextDim)
                } else {
                    auditLogs.take(5).forEach { log ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${log.status} • ${log.actionName}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.status == "REJECTED") NexusCrimson else NexusCyan
                            )
                            Text(
                                text = log.riskLevel,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (log.riskLevel == "HIGH") NexusCrimson else NexusTextDim
                            )
                        }
                    }
                }
            }
        }
    }
}
