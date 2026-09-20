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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NexusScreen
import com.example.ui.NexusViewModel
import com.example.ui.ValidationStatus
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.JarvisArcReactorOrb
import com.example.ui.components.NexusCoreOrb
import com.example.ui.components.NexusCoreState
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
fun InitializationScreen(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val validationStatus by viewModel.validationStatus.collectAsState()
    val isOnline = viewModel.configStore.isCoreOnline()
    val focusManager = LocalFocusManager.current

    var selectedProvider by remember { mutableStateOf(viewModel.configStore.getProvider()) }
    var apiKeyInput by remember { mutableStateOf(viewModel.configStore.getApiKey()) }
    var modelInput by remember { mutableStateOf(viewModel.configStore.getModel()) }
    var baseUrlInput by remember { mutableStateOf(viewModel.configStore.getBaseUrl()) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var showAdvanced by remember { mutableStateOf(false) }

    val providers = listOf("Gemini", "OpenAI", "Groq", "Custom")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // JARVIS Arc Reactor glowing orb in initialization state
        JarvisArcReactorOrb(
            state = if (isOnline) NexusCoreState.IDLE else NexusCoreState.OFFLINE,
            sizeDp = 180.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NEXUS AI",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
            color = NexusCyan
        )

        Text(
            text = "INITIALIZE YOUR AI SYSTEM",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp,
            color = NexusTextSecondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Connect an AI provider to activate NEXUS.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = NexusTextDim
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Offline Guard Notice if offline
        if (!isOnline) {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NexusCrimson.copy(alpha = 0.6f),
                backgroundColor = Color(0x33FF3D71)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Offline Lock",
                            tint = NexusCrimson,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEXUS CORE OFFLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = NexusCrimson,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI services are unavailable until a valid API configuration is provided.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = NexusTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Provider Selector
        Text(
            text = "AI PROVIDER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NexusCyan,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            providers.forEach { p ->
                val isSelected = selectedProvider.equals(p, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NexusCyan.copy(alpha = 0.2f) else NexusSurface)
                        .border(
                            1.dp,
                            if (isSelected) NexusCyan else NexusTextDim.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedProvider = p
                            if (p == "Gemini" && modelInput.isBlank()) modelInput = "gemini-2.5-flash"
                            if (p == "OpenAI" && modelInput.isBlank()) modelInput = "gpt-4o"
                            if (p == "Groq" && modelInput.isBlank()) modelInput = "llama-3.3-70b-versatile"
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = p,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NexusCyan else NexusTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Key Input
        Text(
            text = "API KEY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = NexusCyan,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            placeholder = { Text("Enter $selectedProvider API Key", color = NexusTextDim, fontSize = 13.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Key, contentDescription = "Key", tint = NexusCyan)
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Key Mask",
                        tint = NexusTextSecondary
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NexusCyan,
                unfocusedBorderColor = NexusTextDim.copy(alpha = 0.4f),
                focusedTextColor = NexusTextPrimary,
                unfocusedTextColor = NexusTextPrimary,
                focusedContainerColor = NexusSurface,
                unfocusedContainerColor = NexusSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("api_key_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Advanced Configuration Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAdvanced = !showAdvanced }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Advanced",
                    tint = NexusTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Advanced Configuration",
                    fontSize = 12.sp,
                    color = NexusTextSecondary
                )
            }
            Icon(
                imageVector = if (showAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = "Toggle Advanced",
                tint = NexusTextSecondary
            )
        }

        AnimatedVisibility(visible = showAdvanced) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(
                    text = "MODEL IDENTIFIER",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NexusTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = modelInput,
                    onValueChange = { modelInput = it },
                    placeholder = { Text("e.g. gemini-2.5-flash or gpt-4o", color = NexusTextDim, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexusBlue,
                        unfocusedBorderColor = NexusTextDim.copy(alpha = 0.4f),
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedContainerColor = NexusSurface,
                        unfocusedContainerColor = NexusSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "CUSTOM BASE URL (OPTIONAL)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NexusTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = baseUrlInput,
                    onValueChange = { baseUrlInput = it },
                    placeholder = { Text("https://api.openai.com/v1", color = NexusTextDim, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NexusBlue,
                        unfocusedBorderColor = NexusTextDim.copy(alpha = 0.4f),
                        focusedTextColor = NexusTextPrimary,
                        unfocusedTextColor = NexusTextPrimary,
                        focusedContainerColor = NexusSurface,
                        unfocusedContainerColor = NexusSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Validation Progress / Error Banner
        when (val status = validationStatus) {
            is ValidationStatus.Validating -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexusBlue.copy(alpha = 0.15f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = NexusCyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = status.message,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NexusCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            is ValidationStatus.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexusEmerald.copy(alpha = 0.15f))
                        .border(1.dp, NexusEmerald, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Valid", tint = NexusEmerald)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = status.message,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NexusEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            is ValidationStatus.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NexusCrimson.copy(alpha = 0.15f))
                        .border(1.dp, NexusCrimson, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = "Error", tint = NexusCrimson)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "API KEY INVALID",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NexusCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = status.message,
                        fontSize = 12.sp,
                        color = NexusTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            ValidationStatus.Idle -> {}
        }

        // Validate Button
        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.validateAndInitialize(
                    provider = selectedProvider,
                    apiKey = apiKeyInput,
                    model = modelInput,
                    baseUrl = baseUrlInput
                )
            },
            enabled = apiKeyInput.isNotBlank() && validationStatus !is ValidationStatus.Validating,
            colors = ButtonDefaults.buttonColors(
                containerColor = NexusCyan,
                disabledContainerColor = NexusSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("validate_button")
        ) {
            Text(
                text = "VALIDATE & INITIALIZE",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = if (apiKeyInput.isNotBlank()) NexusVoid else NexusTextDim
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Button to View All App Interfaces
        OutlinedButton(
            onClick = { viewModel.navigateTo(NexusScreen.INTERFACES) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexusCyan),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ViewQuilt, contentDescription = "Interfaces", tint = NexusCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VIEW ALL APP INTERFACES / इंटरफेस देखें",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NexusCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Offline Allowed Navigation Options
        Text(
            text = "Direct screen exploration:",
            fontSize = 11.sp,
            color = NexusTextDim
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { viewModel.navigateTo(NexusScreen.DASHBOARD) }) {
                Text("Dashboard", color = NexusCyan, fontSize = 12.sp)
            }
            Text("•", color = NexusTextDim, modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp))
            TextButton(onClick = { viewModel.navigateTo(NexusScreen.SETTINGS) }) {
                Text("Settings", color = NexusTextSecondary, fontSize = 12.sp)
            }
            Text("•", color = NexusTextDim, modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp))
            TextButton(onClick = { viewModel.navigateTo(NexusScreen.PROJECTS) }) {
                Text("Projects", color = NexusTextSecondary, fontSize = 12.sp)
            }
            Text("•", color = NexusTextDim, modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp))
            TextButton(onClick = { viewModel.navigateTo(NexusScreen.TOOLS) }) {
                Text("Tools", color = NexusTextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Shield, contentDescription = "Security", tint = NexusEmerald, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Credentials stored in isolated local encrypted vault.",
                fontSize = 11.sp,
                color = NexusTextDim
            )
        }
    }
}
