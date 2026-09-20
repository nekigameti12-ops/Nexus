package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.JarvisArcReactorOrb
import com.example.ui.components.NexusCoreOrb
import com.example.ui.components.NexusCoreState
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisCardBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldBright
import com.example.ui.theme.JarvisGoldMuted
import com.example.ui.theme.JarvisOrange
import com.example.ui.theme.JarvisSolarCore
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBlue
import com.example.ui.theme.NexusCrimson
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusViolet
import com.example.ui.theme.NexusVoid

@Composable
fun DashboardScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val coreState by viewModel.coreState.collectAsState()
    val isOnline = viewModel.configStore.isCoreOnline()
    val audioRms by viewModel.voiceEngine.audioRms.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val agentProgress by viewModel.agentProgress.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    var chatInputText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "J.A.R.V.I.S.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp,
                        color = JarvisGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(JarvisOrange.copy(alpha = 0.2f))
                            .border(1.dp, JarvisOrange.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MARK-VI",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = JarvisGold
                        )
                    }
                }
                Text(
                    text = "Just A Rather Very Intelligent System",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = JarvisGoldMuted
                )
            }

            // Online / Offline Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isOnline) NexusEmerald.copy(alpha = 0.15f) else NexusCrimson.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (isOnline) NexusEmerald else NexusCrimson,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        if (!isOnline) viewModel.navigateTo(NexusScreen.INITIALIZATION)
                        else viewModel.navigateTo(NexusScreen.SETTINGS)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) NexusEmerald else NexusCrimson)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) "ONLINE" else "OFFLINE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline) NexusEmerald else NexusCrimson
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Central Animated Core: High-fidelity JARVIS Arc Reactor & Solar Plasma Vortex
        JarvisArcReactorOrb(
            state = coreState,
            audioRms = audioRms,
            sizeDp = 250.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Active Agent Task Bar if working
        AnimatedVisibility(visible = agentProgress != null && agentProgress?.percentage ?: 0 < 100) {
            agentProgress?.let { progress ->
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    borderColor = JarvisGold
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "JARVIS AGENT PROTOCOL ACTIVE",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = JarvisGoldBright
                            )
                            Text(
                                text = "${progress.percentage}%",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = JarvisOrange
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = progress.phaseName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JarvisGoldBright
                        )
                        Text(
                            text = progress.details,
                            fontSize = 11.sp,
                            color = JarvisGoldMuted
                        )
                    }
                }
            }
        }

        // Quick Navigation Grid
        Text(
            text = "COMMAND MODULES",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = JarvisGoldBright,
            letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardTile(
                icon = Icons.Default.ChatBubbleOutline,
                title = "AI Chat",
                subtitle = "Conversational intelligence",
                accentColor = JarvisGold,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.CHAT) }
            )
            DashboardTile(
                icon = Icons.Default.SmartToy,
                title = "Agent Mode",
                subtitle = "Multi-step task planner",
                accentColor = JarvisOrange,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.AGENT) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardTile(
                icon = Icons.Default.FolderOpen,
                title = "Projects",
                subtitle = "Workspace & codebase",
                accentColor = JarvisGoldBright,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.PROJECTS) }
            )
            DashboardTile(
                icon = Icons.Default.Psychology,
                title = "Memory",
                subtitle = "Controlled context vault",
                accentColor = JarvisGold,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.MEMORY) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardTile(
                icon = Icons.Default.Schedule,
                title = "Automate",
                subtitle = "Scheduled workflows",
                accentColor = JarvisOrange,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.AUTOMATIONS) }
            )
            DashboardTile(
                icon = Icons.Default.Widgets,
                title = "Tools Hub",
                subtitle = "Search, code, docs",
                accentColor = JarvisGoldBright,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(NexusScreen.TOOLS) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // All Interfaces & UI Customizer Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(JarvisCardBg)
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(JarvisGold.copy(alpha = 0.6f), JarvisOrange.copy(alpha = 0.6f))),
                    RoundedCornerShape(14.dp)
                )
                .clickable { viewModel.navigateTo(NexusScreen.INTERFACES) }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JarvisGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewQuilt,
                            contentDescription = "Interfaces Directory",
                            tint = JarvisGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "APP INTERFACES & CUSTOMIZER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = JarvisGoldBright
                        )
                        Text(
                            text = "सभी 10 इंटरफेस देखें व रंग/थीम बदलें",
                            fontSize = 11.sp,
                            color = JarvisGoldMuted
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(JarvisGold.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "10 SCREENS",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = JarvisGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live JARVIS Chat Section (Chat me bhi baat ho sake)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(JarvisCardBg)
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(JarvisGold.copy(alpha = 0.5f), JarvisOrange.copy(alpha = 0.2f))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(JarvisGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forum,
                                contentDescription = "JARVIS Chat",
                                tint = JarvisGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "JARVIS NEURAL CHAT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp,
                            color = JarvisGoldBright
                        )
                    }

                    Text(
                        text = "OPEN FULL CHAT →",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = JarvisGold,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(NexusScreen.CHAT) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recent conversation snippet if exists
                val recentMessages = chatMessages.takeLast(2)
                if (recentMessages.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexusVoid.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        recentMessages.forEach { msg ->
                            val isUser = msg.role == "user"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isUser) JarvisGold.copy(alpha = 0.2f)
                                            else JarvisOrange.copy(alpha = 0.15f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isUser) JarvisGold.copy(alpha = 0.4f)
                                            else JarvisOrange.copy(alpha = 0.3f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isUser) "YOU" else "J.A.R.V.I.S.",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) JarvisGoldBright else JarvisOrange
                                    )
                                    Text(
                                        text = msg.content.take(120) + if (msg.content.length > 120) "..." else "",
                                        fontSize = 11.sp,
                                        color = JarvisGoldBright,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Chat Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInputText,
                        onValueChange = { chatInputText = it },
                        placeholder = {
                            Text(
                                text = "Ask JARVIS anything...",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = JarvisGoldMuted.copy(alpha = 0.7f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisGold,
                            unfocusedBorderColor = JarvisGold.copy(alpha = 0.4f),
                            focusedTextColor = JarvisGoldBright,
                            unfocusedTextColor = JarvisGoldBright,
                            cursorColor = JarvisGold,
                            focusedContainerColor = NexusVoid,
                            unfocusedContainerColor = NexusVoid
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (chatInputText.isNotBlank()) {
                                    viewModel.sendChatMessage(chatInputText)
                                    chatInputText = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("dashboard_chat_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (chatInputText.isNotBlank()) {
                                viewModel.sendChatMessage(chatInputText)
                                chatInputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(JarvisGold, JarvisOrange)
                                )
                            )
                            .testTag("dashboard_chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = NexusVoid,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Bottom Voice Button: "TALK TO JARVIS"
        Button(
            onClick = {
                if (!isOnline) {
                    viewModel.navigateTo(NexusScreen.INITIALIZATION)
                } else {
                    if (isListening) {
                        viewModel.stopVoiceListening()
                    } else {
                        viewModel.navigateTo(NexusScreen.VOICE)
                        viewModel.startVoiceListening()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) NexusCrimson else JarvisGold
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("talk_to_nexus_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = NexusVoid,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isListening) "DISENGAGE AUDIO" else "TALK TO JARVIS",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = NexusVoid
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Male voice synthesis • English, Hindi, Gujarati, Hinglish supported",
            fontSize = 11.sp,
            color = JarvisGoldMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DashboardTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(JarvisCardBg)
            .border(
                1.dp,
                Brush.linearGradient(listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = JarvisGoldBright
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = JarvisGoldMuted,
                lineHeight = 14.sp
            )
        }
    }
}
