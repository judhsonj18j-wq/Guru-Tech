package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun EduSmartTopBar(
    currentRole: UserRole,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    val roleColor = when (currentRole) {
        UserRole.TEACHER -> IndigoLight
        UserRole.PARENT -> SkyInfo
        UserRole.STUDENT -> EmeraldSuccess
    }

    val roleBgColor = when (currentRole) {
        UserRole.TEACHER -> IndigoPrimary.copy(alpha = 0.18f)
        UserRole.PARENT -> SkyInfo.copy(alpha = 0.18f)
        UserRole.STUDENT -> EmeraldSuccess.copy(alpha = 0.18f)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        tonalElevation = 6.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header with Icon & Role (Flexible left weight to prevent pushing action buttons)
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(IndigoPrimary, IndigoDark)
                                )
                            )
                            .border(1.dp, IndigoLight.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Guru Tech Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(end = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Guru Tech",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(roleBgColor)
                                    .border(0.8.dp, roleColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = currentRole.displayName.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = roleColor,
                                    letterSpacing = 0.6.sp
                                )
                            }
                        }
                        Text(
                            text = when (currentRole) {
                                UserRole.TEACHER -> "Faculty Desk"
                                UserRole.PARENT -> "Parent Monitoring"
                                UserRole.STUDENT -> "Student Portal"
                            },
                            fontSize = 11.sp,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Dedicated Top Actions Bar (Theme Toggle & Logout Power Button - Perfectly Spaced)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Theme Toggle Button (Day / Night)
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceCardElevated)
                            .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(10.dp))
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkMode) AmberWarning else IndigoLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Power Button (Clean Logout)
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoseError.copy(alpha = 0.12f))
                            .border(1.dp, RoseError.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Sign Out",
                            tint = RoseError,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = colors.surfaceCardBorder.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

