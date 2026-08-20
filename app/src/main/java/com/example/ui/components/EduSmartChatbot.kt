package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.state.EduSmartUiState
import com.example.ui.theme.*
import com.example.util.VoiceAssistantManager

@Composable
fun EduSmartFloatingChatbot(
    uiState: EduSmartUiState,
    onToggleChatbot: () -> Unit,
    onSendMessage: (String, ((String) -> Unit)?) -> Unit,
    onInputChange: (String) -> Unit,
    onToggleAutoVoice: () -> Unit,
    onSetVoiceListening: (Boolean) -> Unit,
    onSetVoiceSpeaking: (Boolean, String?) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voiceManager = remember { VoiceAssistantManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.release()
        }
    }

    val isSpeaking by voiceManager.isSpeaking.collectAsState()
    val isListening by voiceManager.isListening.collectAsState()

    LaunchedEffect(isSpeaking) {
        onSetVoiceSpeaking(isSpeaking, if (isSpeaking) uiState.lastSpokenMessageId else null)
    }

    LaunchedEffect(isListening) {
        onSetVoiceListening(isListening)
    }

    // Floating Button in Bottom Right Corner (Lifted up cleanly from navigation bar and bottom edge)
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(end = 18.dp, bottom = 28.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (!uiState.isChatbotOpen) {
            SmallChatbotLauncherFab(
                onClick = onToggleChatbot,
                isSpeaking = isSpeaking
            )
        }

        if (uiState.isChatbotOpen) {
            ChatbotModalDialog(
                uiState = uiState,
                voiceManager = voiceManager,
                isSpeaking = isSpeaking,
                isListening = isListening,
                onClose = {
                    voiceManager.stopSpeaking()
                    voiceManager.stopListening()
                    onToggleChatbot()
                },
                onSendMessage = { query, onDone ->
                    onSendMessage(query) { answer ->
                        onDone?.invoke(answer)
                        if (uiState.isAutoVoiceResponseEnabled) {
                            voiceManager.speak(answer)
                        }
                    }
                },
                onInputChange = onInputChange,
                onToggleAutoVoice = onToggleAutoVoice,
                onClearChat = {
                    voiceManager.stopSpeaking()
                    onClearChat()
                }
            )
        }
    }
}

/**
 * Small, clean circular FAB launcher button (52dp)
 */
@Composable
fun SmallChatbotLauncherFab(
    onClick: () -> Unit,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        modifier = modifier
            .size(52.dp)
            .scale(pulseScale)
            .shadow(12.dp, CircleShape, spotColor = IndigoPrimary)
            .testTag("chatbot_fab_button")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(IndigoPrimary, Color(0xFF7C3AED), SkyInfo)
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.85f), IndigoLight.copy(alpha = 0.4f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.AutoAwesome,
                contentDescription = "Ask EduAI",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            // Tiny Green Live Web Status Dot in Top Right
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .clip(CircleShape)
                    .background(EmeraldLight)
                    .border(1.5.dp, IndigoDark, CircleShape)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotModalDialog(
    uiState: EduSmartUiState,
    voiceManager: VoiceAssistantManager,
    isSpeaking: Boolean,
    isListening: Boolean,
    onClose: () -> Unit,
    onSendMessage: (String, ((String) -> Unit)?) -> Unit,
    onInputChange: (String) -> Unit,
    onToggleAutoVoice: () -> Unit,
    onClearChat: () -> Unit
) {
    val colors = AppTheme.colors
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening(
                onResult = { spokenText ->
                    if (spokenText.isNotBlank()) {
                        onSendMessage(spokenText, null)
                    }
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Microphone permission is required for voice queries.", Toast.LENGTH_LONG).show()
        }
    }

    fun startVoiceRecognition() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (isListening) {
                voiceManager.stopListening()
            } else {
                voiceManager.stopSpeaking()
                voiceManager.startListening(
                    onResult = { spokenText ->
                        if (spokenText.isNotBlank()) {
                            onSendMessage(spokenText, null)
                        }
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(uiState.chatMessages.size, uiState.isChatbotThinking) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    val suggestedPrompts = listOf(
        "🌐 Latest 2026 AI breakthroughs",
        "📚 Explain Dijkstra's Algorithm",
        "🎓 Govt Scholarships in India",
        "💻 Python Binary Search Tree code",
        "⚡ Quantum Computing in simple terms",
        "📖 EduSmart attendance rules"
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 12.dp, end = 12.dp, top = 20.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .shadow(24.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = colors.surfaceCardElevated,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        listOf(colors.surfaceCardBorder, IndigoLight.copy(alpha = 0.5f))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Bar with Voice Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(IndigoDark, IndigoPrimary)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Public,
                                    contentDescription = "Internet Voice AI",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "EduAI Voice & Web",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.25f),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = Brush.horizontalGradient(listOf(EmeraldLight, EmeraldSuccess))
                                        )
                                    ) {
                                        Text(
                                            text = "VOICE + WEB",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isSpeaking) "Speaking response aloud..." else if (isListening) "Listening to your voice..." else "Speak or type any question",
                                    fontSize = 11.sp,
                                    color = if (isSpeaking || isListening) EmeraldLight else Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Right Action Controls: Voice Auto Toggle, Clear, Close
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Toggle Voice Out
                            IconButton(
                                onClick = {
                                    if (isSpeaking) {
                                        voiceManager.stopSpeaking()
                                    }
                                    onToggleAutoVoice()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isAutoVoiceResponseEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Toggle Voice Response",
                                    tint = if (uiState.isAutoVoiceResponseEnabled) EmeraldLight else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = onClearChat,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Clear Chat",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Active Voice Banner (When speaking or listening)
                    if (isListening || isSpeaking) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isListening) RoseError.copy(alpha = 0.15f) else IndigoPrimary.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = if (isListening) RoseError else IndigoLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isListening) "🎙️ Listening... Speak your question now" else "🔊 Speaking answer aloud...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        if (isListening) voiceManager.stopListening()
                                        if (isSpeaking) voiceManager.stopSpeaking()
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Stop", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoseError)
                                }
                            }
                        }
                    }

                    // Suggested Prompts Carousel
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceCard)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(suggestedPrompts) { prompt ->
                            Surface(
                                onClick = {
                                    val clean = prompt.substringAfter(" ")
                                    onSendMessage(clean, null)
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = colors.surfaceCardElevated,
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(
                                        listOf(colors.surfaceCardBorder, IndigoLight.copy(alpha = 0.3f))
                                    )
                                )
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = colors.surfaceCardBorder, thickness = 0.5.dp)

                    // Chat Messages List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(colors.background)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.chatMessages, key = { it.id }) { message ->
                            ChatBubbleItem(
                                message = message,
                                isSpeakingThis = isSpeaking && uiState.lastSpokenMessageId == message.id,
                                onSpeak = {
                                    voiceManager.speak(message.text)
                                },
                                onStopSpeaking = {
                                    voiceManager.stopSpeaking()
                                },
                                onCopy = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        if (uiState.isChatbotThinking) {
                            item {
                                ThinkingBubbleItem()
                            }
                        }
                    }

                    HorizontalDivider(color = colors.surfaceCardBorder, thickness = 0.5.dp)

                    // Bottom Input & Voice Mic Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceCardElevated
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Dedicated Voice Mic Button
                            val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
                            val micScale by infiniteTransition.animateFloat(
                                initialValue = 1.0f,
                                targetValue = if (isListening) 1.25f else 1.0f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "micScale"
                            )

                            IconButton(
                                onClick = { startVoiceRecognition() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .scale(micScale)
                                    .clip(CircleShape)
                                    .background(
                                        if (isListening) RoseError else IndigoPrimary.copy(alpha = 0.18f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isListening) RoseError else IndigoPrimary.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                                    .testTag("chatbot_voice_mic_button")
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = if (isListening) Color.White else IndigoPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Text Input
                            OutlinedTextField(
                                value = uiState.chatbotInputText,
                                onValueChange = onInputChange,
                                placeholder = {
                                    Text(
                                        text = if (isListening) "Listening..." else "Ask or speak any question...",
                                        fontSize = 12.sp,
                                        color = colors.textMuted
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chatbot_input_field"),
                                singleLine = false,
                                maxLines = 3,
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IndigoPrimary,
                                    unfocusedBorderColor = colors.surfaceCardBorder,
                                    focusedContainerColor = colors.background,
                                    unfocusedContainerColor = colors.background,
                                    focusedTextColor = colors.textPrimary,
                                    unfocusedTextColor = colors.textPrimary
                                )
                            )

                            // Send Button
                            IconButton(
                                onClick = {
                                    if (uiState.chatbotInputText.isNotBlank()) {
                                        onSendMessage(uiState.chatbotInputText, null)
                                    }
                                },
                                enabled = uiState.chatbotInputText.isNotBlank() && !uiState.isChatbotThinking,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (uiState.chatbotInputText.isNotBlank()) IndigoPrimary else colors.surfaceCardBorder
                                    )
                                    .testTag("chatbot_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = if (uiState.chatbotInputText.isNotBlank()) Color.White else colors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    isSpeakingThis: Boolean,
    onSpeak: () -> Unit,
    onStopSpeaking: () -> Unit,
    onCopy: () -> Unit
) {
    val colors = AppTheme.colors
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender Header Tag
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = if (isUser) Icons.Default.Person else Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (isUser) IndigoPrimary else EmeraldSuccess,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (isUser) "You" else "EduAI • Web Knowledge",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Text(
                text = "• ${message.timestamp}",
                fontSize = 9.sp,
                color = colors.textMuted
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) IndigoPrimary else colors.surfaceCard,
            border = if (!isUser) {
                ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        listOf(colors.surfaceCardBorder, IndigoLight.copy(alpha = 0.25f))
                    )
                )
            } else null,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = formatMarkdownToAnnotatedString(message.text, isUser, colors.textPrimary),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = if (isUser) Color.White else colors.textPrimary
                    )
                }

                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.isWebGrounded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Internet Grounded",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EmeraldSuccess
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Voice Speak / Stop Button per message
                            IconButton(
                                onClick = {
                                    if (isSpeakingThis) {
                                        onStopSpeaking()
                                    } else {
                                        onSpeak()
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeakingThis) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = if (isSpeakingThis) RoseError else IndigoLight,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Copy Button
                            IconButton(
                                onClick = onCopy,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy text",
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThinkingBubbleItem() {
    val colors = AppTheme.colors

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceCard,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(
                listOf(colors.surfaceCardBorder, IndigoLight.copy(alpha = 0.4f))
            )
        ),
        shadowElevation = 2.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = IndigoPrimary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "🌐 Searching web & synthesizing answer...",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textSecondary
            )
        }
    }
}

fun formatMarkdownToAnnotatedString(
    text: String,
    isUser: Boolean,
    textColor: Color
): AnnotatedString {
    val parts = text.split("**")
    return buildAnnotatedString {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White else IndigoLight
                    )
                ) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}
