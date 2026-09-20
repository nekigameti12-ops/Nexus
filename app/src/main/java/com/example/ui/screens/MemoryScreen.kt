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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.MemoryItemEntity
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.NexusCardBg
import com.example.ui.theme.NexusCrimson
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusTextPrimary
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusVoid

@Composable
fun MemoryScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val memoryItems by viewModel.memoryItems.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
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
                        text = "NEURAL MEMORY VAULT",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NexusCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Transparent context & user facts",
                        fontSize = 11.sp,
                        color = NexusTextSecondary
                    )
                }
            }

            Row {
                IconButton(onClick = { viewModel.clearAllMemory() }) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = NexusCrimson)
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NexusEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp), tint = NexusVoid)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("STORE", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NexusVoid)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (memoryItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = "Memory", tint = NexusTextDim, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("MEMORY VAULT EMPTY", fontFamily = FontFamily.Monospace, color = NexusTextSecondary, fontSize = 13.sp)
                    Text("Add user preferences or let NEXUS store verified facts.", color = NexusTextDim, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(memoryItems) { item ->
                    MemoryCard(
                        item = item,
                        onDelete = { viewModel.deleteMemoryItem(item.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddMemoryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { category, key, value ->
                viewModel.addMemoryItem(category, key, value)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MemoryCard(
    item: MemoryItemEntity,
    onDelete: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NexusEmerald.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.uppercase(),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NexusEmerald
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.key,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NexusTextPrimary
                )
                Text(
                    text = item.value,
                    fontSize = 12.sp,
                    color = NexusTextSecondary
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NexusTextDim, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AddMemoryDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var category by remember { mutableStateOf("Preference") }
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NexusCardBg)
                .border(1.dp, NexusEmerald, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "STORE MEMORY ITEM",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NexusEmerald
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("Memory Key (e.g. Favorite Language)", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Value (e.g. Kotlin & Jetpack Compose)", fontSize = 11.sp) },
                maxLines = 3,
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
                        if (key.isNotBlank() && value.isNotBlank()) onAdd(category, key, value)
                    },
                    enabled = key.isNotBlank() && value.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NexusEmerald)
                ) {
                    Text("STORE", fontSize = 11.sp, color = NexusVoid, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
