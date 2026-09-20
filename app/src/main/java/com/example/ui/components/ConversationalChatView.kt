package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.ui.theme.JarvisBorderGlow
import com.example.ui.theme.JarvisCardBg
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGoldBright
import com.example.ui.theme.JarvisGoldMuted
import com.example.ui.theme.JarvisOrange
import com.example.ui.theme.NexusSurfaceVariant
import com.example.ui.theme.NexusTextDim
import com.example.ui.theme.NexusVoid
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Model representing a conversational message for generic usages.
 */
data class ConversationalMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val senderName: String? = null,
    val tag: String? = null
)

/**
 * High-performance, accessible conversational UI component in Jetpack Compose.
 * Displays:
 * 1. Scrollable message list with auto-scroll and jump-to-bottom capability
 * 2. Formatted conversational message bubbles (User vs Assistant)
 * 3. Animated loading/reasoning indicator
 * 4. Multi-line capable input text field with Send button and optional Voice action
 */
@Composable
fun ConversationalChatView(
    messages: List<ConversationalMessage>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    isTyping: Boolean = false,
    placeholder: String = "Type a message...",
    emptyStateTitle: String = "CONVERSATION READY",
    emptyStateSubtitle: String = "Ask anything or issue commands to get started.",
    quickSuggestions: List<String> = emptyList(),
    onVoiceClick: (() -> Unit)? = null,
    onSpeakMessage: ((String) -> Unit)? = null
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    // Auto-scroll to the bottom when new message arrives or typing state starts
    LaunchedEffect(messages.size, isTyping) {
        val totalCount = messages.size + if (isTyping) 1 else 0
        if (totalCount > 0) {
            listState.animateScrollToItem(totalCount - 1)
        }
    }

    // Check if user has scrolled up away from the bottom
    val isScrolledUp by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems > 3 && lastVisible < totalItems - 2
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexusVoid)
    ) {
        // Message Stream Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("conversational_message_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        EmptyChatState(
                            title = emptyStateTitle,
                            subtitle = emptyStateSubtitle,
                            suggestions = quickSuggestions,
                            onSelectSuggestion = { suggestion ->
                                onSendMessage(suggestion)
                            }
                        )
                    }
                } else {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { msg ->
                        ConversationalBubble(
                            message = msg,
                            onCopy = { clipboardManager.setText(AnnotatedString(msg.text)) },
                            onSpeak = onSpeakMessage?.let { speak -> { speak(msg.text) } }
                        )
                    }
                }

                if (isTyping) {
                    item {
                        TypingIndicatorBubble()
                    }
                }
            }

            // Scroll to bottom floating action
            androidx.compose.animation.AnimatedVisibility(
                visible = isScrolledUp,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val totalItems = listState.layoutInfo.totalItemsCount
                            if (totalItems > 0) {
                                listState.animateScrollToItem(totalItems - 1)
                            }
                        }
                    },
                    containerColor = JarvisCardBg,
                    contentColor = JarvisGoldBright,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, JarvisBorderGlow, CircleShape)
                        .testTag("scroll_to_bottom_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Input Bar Area with Send Button
        ConversationalInputBar(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = placeholder,
            onSend = {
                val trimmed = textInput.trim()
                if (trimmed.isNotEmpty() && !isTyping) {
                    onSendMessage(trimmed)
                    textInput = ""
                    focusManager.clearFocus()
                }
            },
            isSendEnabled = textInput.trim().isNotEmpty() && !isTyping,
            onVoiceClick = onVoiceClick
        )
    }
}

/**
 * Overload of ConversationalChatView accepting ChatMessageEntity for direct Room DB integration.
 */
@JvmName("ConversationalChatViewFromEntities")
@Composable
fun ConversationalChatView(
    messages: List<ChatMessageEntity>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    isTyping: Boolean = false,
    placeholder: String = "Command JARVIS...",
    emptyStateTitle: String = "J.A.R.V.I.S. READY",
    emptyStateSubtitle: String = "Ask questions, generate software, or command protocols.",
    quickSuggestions: List<String> = listOf(
        "Explain quantum computing simply",
        "Plan a full-stack portfolio architecture",
        "Write a Python script for web automation",
        "Status report of all system protocols"
    ),
    onVoiceClick: (() -> Unit)? = null,
    onSpeakMessage: ((String) -> Unit)? = null
) {
    val convertedMessages = remember(messages) {
        messages.map { entity ->
            ConversationalMessage(
                id = entity.id.toString(),
                text = entity.content,
                isUser = entity.role.equals("user", ignoreCase = true),
                timestamp = entity.timestamp,
                senderName = if (entity.role.equals("user", ignoreCase = true)) "COMMAND" else "J.A.R.V.I.S.",
                tag = if (entity.mode.isNotBlank() && entity.mode != "chat") entity.mode.uppercase() else null
            )
        }
    }

    ConversationalChatView(
        messages = convertedMessages,
        onSendMessage = onSendMessage,
        modifier = modifier,
        isTyping = isTyping,
        placeholder = placeholder,
        emptyStateTitle = emptyStateTitle,
        emptyStateSubtitle = emptyStateSubtitle,
        quickSuggestions = quickSuggestions,
        onVoiceClick = onVoiceClick,
        onSpeakMessage = onSpeakMessage
    )
}

/**
 * Message bubble supporting distinct User vs Assistant layouts and actions.
 */
@Composable
private fun ConversationalBubble(
    message: ConversationalMessage,
    onCopy: () -> Unit,
    onSpeak: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (isUser) "user_message_bubble" else "assistant_message_bubble"),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender info & Mode badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = message.senderName ?: if (isUser) "YOU" else "J.A.R.V.I.S.",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (isUser) JarvisGoldBright else JarvisOrange
            )

            message.tag?.let { tag ->
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• $tag",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = JarvisGold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedTime,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = JarvisGoldMuted.copy(alpha = 0.7f)
            )
        }

        // Message Bubble Container
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) JarvisGold.copy(alpha = 0.18f) else JarvisCardBg)
                .border(
                    1.dp,
                    if (isUser) JarvisGold.copy(alpha = 0.5f) else JarvisBorderGlow,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = JarvisGoldBright,
                    lineHeight = 19.sp
                )

                // Actions toolbar for messages
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isUser) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("copy_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy text",
                                    tint = JarvisGoldMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            if (onSpeak != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = onSpeak,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("read_aloud_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Read aloud",
                                        tint = JarvisGold,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isUser) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Delivered",
                            tint = JarvisGold.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pulsing animated typing indicator representing system reasoning.
 */
@Composable
private fun TypingIndicatorBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(JarvisCardBg)
            .border(1.dp, JarvisBorderGlow, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("typing_indicator")
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(alpha1)
                .clip(CircleShape)
                .background(JarvisGold)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(alpha2)
                .clip(CircleShape)
                .background(JarvisOrange)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(alpha3)
                .clip(CircleShape)
                .background(JarvisGoldBright)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "JARVIS IS PROCESSING...",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = JarvisGoldBright
        )
    }
}

/**
 * Clean empty state with title, subtitle, and clickable suggestion prompts.
 */
@Composable
private fun EmptyChatState(
    title: String,
    subtitle: String,
    suggestions: List<String>,
    onSelectSuggestion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(JarvisGold.copy(alpha = 0.15f))
                .border(1.dp, JarvisBorderGlow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "Assistant Ready",
                tint = JarvisGold,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = JarvisGoldBright,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = JarvisGoldMuted
        )

        if (suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(JarvisCardBg)
                            .border(1.dp, JarvisGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable { onSelectSuggestion(prompt) }
                            .padding(14.dp)
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            color = JarvisGoldBright,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Accessible, responsive input bar with text field and send button.
 */
@Composable
private fun ConversationalInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onSend: () -> Unit,
    isSendEnabled: Boolean,
    onVoiceClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JarvisCardBg)
            .border(
                1.dp,
                Brush.verticalGradient(listOf(JarvisBorderGlow, Color.Transparent)),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = JarvisGoldMuted.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            },
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (isSendEnabled) {
                        onSend()
                    }
                }
            ),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear text",
                            tint = JarvisGoldMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JarvisGold,
                unfocusedBorderColor = JarvisGold.copy(alpha = 0.35f),
                focusedTextColor = JarvisGoldBright,
                unfocusedTextColor = JarvisGoldBright,
                cursorColor = JarvisGold,
                focusedContainerColor = NexusVoid,
                unfocusedContainerColor = NexusVoid
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("conversational_input_field")
        )

        if (onVoiceClick != null) {
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onVoiceClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NexusSurfaceVariant)
                    .testTag("conversational_voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = JarvisGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
            onClick = onSend,
            enabled = isSendEnabled,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSendEnabled) {
                        Brush.linearGradient(listOf(JarvisGold, JarvisOrange))
                    } else {
                        Brush.linearGradient(listOf(NexusSurfaceVariant, NexusSurfaceVariant))
                    }
                )
                .testTag("conversational_send_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send message",
                tint = if (isSendEnabled) NexusVoid else NexusTextDim,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
