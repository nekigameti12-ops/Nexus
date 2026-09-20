package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
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
fun AgentScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val agentProgress by viewModel.agentProgress.collectAsState()
    val isWorking by viewModel.agentEngine.isWorking.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    val lastAgentMessage = messages.lastOrNull { it.mode == "agent" && it.role == "nexus" }

    var taskPrompt by remember {
        mutableStateOf("Plan and generate architecture for a real-time collaborative task manager with Kotlin backend")
    }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NexusCyan)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "NEXUS AGENT MODE",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = NexusCyan
                )
                Text(
                    text = "Autonomous Plan & Execution Engine",
                    fontSize = 11.sp,
                    color = NexusTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task Formulation Box
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NexusViolet.copy(alpha = 0.5f)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Agent", tint = NexusViolet)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AUTONOMOUS GOAL SPECIFICATION",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NexusViolet,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = taskPrompt,
                    onValueChange = { taskPrompt = it },
                    placeholder = { Text("Enter multi-step autonomous goal...", color = NexusTextDim, fontSize = 13.sp) },
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexusViolet,
                        unfocusedBorderColor = NexusTextDim.copy(alpha = 0.3f),
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedContainerColor = NexusSurface,
                        unfocusedContainerColor = NexusSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("agent_goal_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.runAgentTask(taskPrompt)
                    },
                    enabled = taskPrompt.isNotBlank() && !isWorking,
                    colors = ButtonDefaults.buttonColors(containerColor = NexusViolet),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isWorking) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXECUTING AUTONOMOUSLY...", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EXECUTE AGENT WORKFLOW", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-Step Progress Timeline
        Text(
            text = "EXECUTION LIFECYCLE (HEAR → PLAN → EXECUTE → VERIFY)",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = NexusTextDim,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        val currentPercentage = agentProgress?.percentage ?: 0

        LinearProgressIndicator(
            progress = { (currentPercentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NexusCyan,
            trackColor = NexusSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        val steps = listOf(
            20 to "Plan the task & architecture",
            40 to "Search & contextual synthesis",
            70 to "Execute tools & compile artifacts",
            90 to "Verify consistency & security",
            100 to "Deliver verified results"
        )

        steps.forEach { (pct, label) ->
            val isPassed = currentPercentage >= pct
            val isCurrent = currentPercentage in (pct - 19)..pct

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPassed) NexusEmerald
                            else if (isCurrent && isWorking) NexusAmber
                            else NexusSurface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done", tint = NexusVoid, modifier = Modifier.size(14.dp))
                    } else {
                        Text(text = "$pct%", fontSize = 8.sp, color = NexusTextDim)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isPassed || isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isPassed) NexusEmerald else if (isCurrent) NexusCyan else NexusTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generated Deliverables Artifact Box
        if (lastAgentMessage != null) {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NexusCyan.copy(alpha = 0.5f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TaskAlt, contentDescription = "Deliverable", tint = NexusCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEXUS ARTIFACT DELIVERABLE",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NexusCyan
                            )
                        }

                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(lastAgentMessage.content)) }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NexusTextDim, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = lastAgentMessage.content,
                        fontSize = 13.sp,
                        color = NexusTextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
