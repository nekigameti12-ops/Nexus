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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBlue
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusViolet
import com.example.ui.theme.NexusVoid

@Composable
fun ToolsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    var smartBulbOn by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
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
                    text = "NEXUS TOOL MATRIX",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NexusCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Connected neural capability plugins",
                    fontSize = 11.sp,
                    color = NexusTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool 1: Web Search Engine
        ToolItemCard(
            icon = Icons.Default.Language,
            title = "Web Intelligence & Search",
            description = "Real-time web retrieval, documentation indexing, and factual synthesis.",
            status = "ACTIVE",
            color = NexusCyan,
            actionLabel = "QUERY WEB",
            onAction = {
                viewModel.sendChatMessage("Search the web for the latest advancements in AI agents and summarize top insights.", mode = "research")
                viewModel.navigateTo(NexusScreen.CHAT)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 2: Nexus Coder Sandbox
        ToolItemCard(
            icon = Icons.Default.Code,
            title = "Nexus Coder & Sandbox",
            description = "Write, analyze, refactor, and verify Kotlin, Python, JS, HTML, and SQL.",
            status = "ACTIVE",
            color = NexusBlue,
            actionLabel = "OPEN CODER",
            onAction = {
                viewModel.sendChatMessage("Generate a high-performance Kotlin algorithm with time complexity analysis.", mode = "coder")
                viewModel.navigateTo(NexusScreen.CHAT)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 3: Math & Logic Engine
        ToolItemCard(
            icon = Icons.Default.Calculate,
            title = "Precision Math Engine",
            description = "High-precision calculations, algorithmic complexity, statistics, and conversions.",
            status = "READY",
            color = NexusViolet,
            actionLabel = "CALCULATE",
            onAction = {
                viewModel.sendChatMessage("Calculate compound interest and future value table for $500/month at 8% annual return over 25 years.")
                viewModel.navigateTo(NexusScreen.CHAT)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 4: Document Creator & PDF Intelligence
        ToolItemCard(
            icon = Icons.Default.Description,
            title = "Document & File Intelligence",
            description = "Synthesize structured reports, markdown guides, technical specifications.",
            status = "ACTIVE",
            color = NexusEmerald,
            actionLabel = "CREATE REPORT",
            onAction = {
                viewModel.sendChatMessage("Draft a comprehensive technical architecture document for a distributed messaging service.")
                viewModel.navigateTo(NexusScreen.CHAT)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tool 5: IoT & Smart Device Hub (Interactive)
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (smartBulbOn) NexusAmber else NexusTextDim.copy(alpha = 0.3f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "IoT", tint = if (smartBulbOn) NexusAmber else NexusTextDim)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SMART DEVICE CONTROLLER",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NexusTextPrimary
                        )
                    }

                    Text(
                        text = if (smartBulbOn) "LIGHT ON" else "STANDBY",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (smartBulbOn) NexusAmber else NexusTextDim
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Control local smart home fixtures, IoT bulbs, and ambient lighting protocols.",
                    fontSize = 12.sp,
                    color = NexusTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = { smartBulbOn = !smartBulbOn },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (smartBulbOn) "TURN OFF" else "TURN ON BULB",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (smartBulbOn) NexusAmber else NexusCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    status: String,
    color: androidx.compose.ui.graphics.Color,
    actionLabel: String,
    onAction: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = color.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NexusTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = NexusTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = color
                    )
                }
            }
        }
    }
}
