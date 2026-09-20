package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.AutomationEntity
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusCardBg
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusSurface
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusVoid

@Composable
fun AutomationsScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val automations by viewModel.automations.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NexusCyan)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "NEXUS AUTOMATE",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NexusCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Trigger • Condition • Action",
                        fontSize = 11.sp,
                        color = NexusTextSecondary
                    )
                }
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NexusAmber),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp), tint = NexusVoid)
                Spacer(modifier = Modifier.width(4.dp))
                Text("NEW WORKFLOW", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NexusVoid)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (automations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Automate", tint = NexusTextDim, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("NO WORKFLOWS SCHEDULED", fontFamily = FontFamily.Monospace, color = NexusTextSecondary, fontSize = 13.sp)
                    Text("Automate recurring briefs, research, and reminders.", color = NexusTextDim, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(automations) { item ->
                    AutomationCard(
                        automation = item,
                        onToggle = { viewModel.toggleAutomation(item) },
                        onDelete = { viewModel.deleteAutomation(item.id) },
                        onTriggerNow = {
                            viewModel.sendChatMessage("Trigger automation workflow now: ${item.actionDesc}", mode = "agent")
                            viewModel.navigateTo(NexusScreen.CHAT)
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAutomationDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, trigger, cond, act, sched ->
                viewModel.createAutomation(title, trigger, cond, act, sched)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun AutomationCard(
    automation: AutomationEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTriggerNow: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (automation.isActive) NexusAmber.copy(alpha = 0.5f) else NexusTextDim.copy(alpha = 0.2f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = "Time", tint = NexusAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = automation.scheduleTime,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NexusAmber
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = automation.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NexusAmber,
                            checkedTrackColor = NexusAmber.copy(alpha = 0.3f),
                            uncheckedThumbColor = NexusTextDim,
                            uncheckedTrackColor = NexusSurface
                        )
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NexusTextDim, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = automation.title,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NexusTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Trigger: ${automation.triggerDesc}",
                fontSize = 11.sp,
                color = NexusTextSecondary
            )
            Text(
                text = "Action: ${automation.actionDesc}",
                fontSize = 11.sp,
                color = NexusCyan
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onTriggerNow,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("TRIGGER NOW", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NexusAmber)
                }
            }
        }
    }
}

@Composable
private fun CreateAutomationDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf("08:00 AM Daily") }
    var condition by remember { mutableStateOf("Device Connected") }
    var action by remember { mutableStateOf("Search tech news & summarize") }
    var schedule by remember { mutableStateOf("Every day at 8:00 AM") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NexusCardBg)
                .border(1.dp, NexusAmber, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "SCHEDULE AUTOMATION",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NexusAmber
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (e.g. Morning Brief)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                label = { Text("Action to execute", fontSize = 11.sp) },
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = schedule,
                onValueChange = { schedule = it },
                label = { Text("Schedule Description", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onDismiss) {
                    Text("CANCEL", fontSize = 11.sp, color = NexusTextSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) onCreate(title, trigger, condition, action, schedule)
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NexusAmber)
                ) {
                    Text("SAVE", fontSize = 11.sp, color = NexusVoid, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
