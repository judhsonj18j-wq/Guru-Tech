package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.ToastMessage
import com.example.state.ToastType
import com.example.ui.theme.*

@Composable
fun ToastNotificationHost(
    toast: ToastMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.9f)
        ) + fadeOut(),
        modifier = modifier
    ) {
        if (toast != null) {
            val (icon, bgBorder, accentColor) = when (toast.type) {
                ToastType.SUCCESS -> Triple(
                    Icons.Default.CheckCircle,
                    EmeraldSuccess.copy(alpha = 0.5f),
                    EmeraldSuccess
                )
                ToastType.INFO -> Triple(
                    Icons.Default.Info,
                    IndigoLight.copy(alpha = 0.5f),
                    IndigoLight
                )
                ToastType.WARNING -> Triple(
                    Icons.Default.Warning,
                    AmberWarning.copy(alpha = 0.5f),
                    AmberWarning
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCardElevated
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SurfaceCardElevated,
                                    SurfaceCard
                                )
                            )
                        )
                        .border(1.dp, bgBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Notification",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (toast.type) {
                                    ToastType.SUCCESS -> "Action Confirmed"
                                    ToastType.INFO -> "EduSmart Notice"
                                    ToastType.WARNING -> "Notice"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = toast.message,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
