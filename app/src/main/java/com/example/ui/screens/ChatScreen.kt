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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.ConversationalChatView
import com.example.ui.theme.JarvisCardBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldBright
import com.example.ui.theme.JarvisGoldMuted
import com.example.ui.theme.NexusVoid

@Composable
fun ChatScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isWorking by viewModel.agentEngine.isWorking.collectAsState()
    var activeMode by remember { mutableStateOf("chat") }

    val modes = listOf("chat" to "Chat", "coder" to "Coder", "research" to "Research", "study" to "Study")

    val suggestions = listOf(
        "Write a Python script for web scraping",
        "Plan a full-stack portfolio architecture",
        "Explain quantum computing simply",
        "Nexus, summarize this topic in Gujarati"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JarvisGold
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "J.A.R.V.I.S. CONVERSATION",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = JarvisGoldBright,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Arc Reactor Neural Link Active",
                        fontSize = 11.sp,
                        color = JarvisGoldMuted
                    )
                }
            }

            IconButton(onClick = { viewModel.clearChatHistory() }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = JarvisGoldMuted
                )
            }
        }

        // Sub-mode pills (Chat / Coder / Research / Study)
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(modes) { (mKey, mLabel) ->
                val isSelected = activeMode == mKey
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) JarvisGold.copy(alpha = 0.25f) else JarvisCardBg)
                        .border(1.dp, if (isSelected) JarvisGold else JarvisGold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .clickable { activeMode = mKey }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = mLabel,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) JarvisGoldBright else JarvisGoldMuted
                    )
                }
            }
        }

        // Reusable Conversational UI Component: scrollable list of messages + input field with send button
        ConversationalChatView(
            messages = messages,
            onSendMessage = { text -> viewModel.sendChatMessage(text, activeMode) },
            isTyping = isWorking,
            placeholder = "Command JARVIS in ${activeMode.replaceFirstChar { it.uppercase() }} mode...",
            emptyStateTitle = "J.A.R.V.I.S. READY",
            emptyStateSubtitle = "Ask questions, generate software, or command protocols.",
            quickSuggestions = suggestions,
            onVoiceClick = {
                viewModel.navigateTo(NexusScreen.VOICE)
                viewModel.startVoiceListening()
            },
            onSpeakMessage = { text -> viewModel.voiceEngine.speak(text) },
            modifier = Modifier.weight(1f)
        )
    }
}
