package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.state.EduSmartUiState
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    uiState: EduSmartUiState,
    onRoleSelected: (UserRole) -> Unit,
    onMobileChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onStudentIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onBypassLogin: () -> Unit,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var isPasswordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Subtle background glowing ambient orbs
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            IndigoPrimary.copy(alpha = if (uiState.isDarkMode) 0.2f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldSuccess.copy(alpha = if (uiState.isDarkMode) 0.12f else 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top Right Theme Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onToggleDarkMode,
                shape = RoundedCornerShape(20.dp),
                color = colors.surfaceCardElevated,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(colors.surfaceCardBorder, IndigoLight.copy(alpha = 0.4f)))
                ),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("login_theme_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = if (uiState.isDarkMode) AmberWarning else IndigoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (uiState.isDarkMode) "Light Mode" else "Dark Mode",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Branding Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(IndigoPrimary, IndigoDark)
                        )
                    )
                    .border(1.5.dp, IndigoLight.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Guru Tech",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Guru Tech",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Unified Access for Faculty, Guardians & Students",
                fontSize = 13.sp,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Segmented Role Control Tab
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = colors.surfaceCardElevated,
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = uiState.loginTabRole == role
                        val backgroundModifier = if (isSelected) {
                            Modifier.background(Brush.horizontalGradient(listOf(IndigoPrimary, IndigoSecondary)))
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .then(backgroundModifier)
                                .clickable { onRoleSelected(role) }
                                .padding(vertical = 10.dp)
                                .testTag("role_tab_${role.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.TEACHER -> Icons.Default.Person
                                        UserRole.PARENT -> Icons.Default.FamilyRestroom
                                        UserRole.STUDENT -> Icons.Default.Face
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = role.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Role Context Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = colors.surfaceCard
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    // Header inside card indicating the chosen role
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (uiState.loginTabRole) {
                                        UserRole.TEACHER -> IndigoLight
                                        UserRole.PARENT -> SkyInfo
                                        UserRole.STUDENT -> EmeraldSuccess
                                    }
                                )
                        )
                        Text(
                            text = "${uiState.loginTabRole.displayName} Authentication",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = uiState.loginTabRole.badge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedContent(
                        targetState = uiState.loginTabRole,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "LoginFormAnim"
                    ) { targetRole ->
                        when (targetRole) {
                            UserRole.TEACHER, UserRole.PARENT -> {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    // Mobile Number Field
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Registered Mobile Number",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.textSecondary
                                        )
                                        OutlinedTextField(
                                            value = uiState.mobileNumberInput,
                                            onValueChange = onMobileChange,
                                            placeholder = { Text("+1 (555) 019-2834", color = colors.textMuted) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = IndigoLight
                                                )
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = IndigoPrimary,
                                                unfocusedBorderColor = colors.surfaceCardBorder,
                                                focusedContainerColor = colors.surfaceCardElevated,
                                                unfocusedContainerColor = colors.surfaceCardElevated,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("mobile_input")
                                        )
                                    }

                                    // 6-digit OTP passcode box
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "6-Digit OTP Passcode",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = colors.textSecondary
                                            )
                                            Text(
                                                text = "Auto-filled ✓",
                                                fontSize = 11.sp,
                                                color = EmeraldLight,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        OutlinedTextField(
                                            value = uiState.otpInput,
                                            onValueChange = { if (it.length <= 6) onOtpChange(it) },
                                            placeholder = { Text("• • • • • •", color = colors.textMuted) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = IndigoLight
                                                )
                                            },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = IndigoPrimary,
                                                unfocusedBorderColor = colors.surfaceCardBorder,
                                                focusedContainerColor = colors.surfaceCardElevated,
                                                unfocusedContainerColor = colors.surfaceCardElevated,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("otp_input")
                                        )
                                    }
                                }
                            }

                            UserRole.STUDENT -> {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    // User ID / Registration Number
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Student Registration ID / Roll No.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.textSecondary
                                        )
                                        OutlinedTextField(
                                            value = uiState.studentIdInput,
                                            onValueChange = onStudentIdChange,
                                            placeholder = { Text("EDU-2026-STU881", color = colors.textMuted) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Badge,
                                                    contentDescription = null,
                                                    tint = EmeraldLight
                                                )
                                            },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = EmeraldSuccess,
                                                unfocusedBorderColor = colors.surfaceCardBorder,
                                                focusedContainerColor = colors.surfaceCardElevated,
                                                unfocusedContainerColor = colors.surfaceCardElevated,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("student_id_input")
                                        )
                                    }

                                    // Password Field
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Secure Password",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = colors.textSecondary
                                        )
                                        OutlinedTextField(
                                            value = uiState.passwordInput,
                                            onValueChange = onPasswordChange,
                                            placeholder = { Text("Enter password", color = colors.textMuted) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.VpnKey,
                                                    contentDescription = null,
                                                    tint = EmeraldLight
                                                )
                                            },
                                            trailingIcon = {
                                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                                    Icon(
                                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                        contentDescription = "Toggle Password",
                                                        tint = colors.textMuted
                                                    )
                                                }
                                            },
                                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = EmeraldSuccess,
                                                unfocusedBorderColor = colors.surfaceCardBorder,
                                                focusedContainerColor = colors.surfaceCardElevated,
                                                unfocusedContainerColor = colors.surfaceCardElevated,
                                                focusedTextColor = colors.textPrimary,
                                                unfocusedTextColor = colors.textPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("password_input")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Universal Bypass Banner notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.12f))
                            .border(1.dp, IndigoLight.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Bypass active",
                                tint = AmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Universal Bypass Active: Tap button below to instantly load the ${uiState.loginTabRole.displayName} view.",
                                fontSize = 11.sp,
                                color = colors.textPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // CRITICAL UNIVERSAL BYPASS LOGIN BUTTON
                    Button(
                        onClick = onBypassLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(12.dp, RoundedCornerShape(14.dp))
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IndigoPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Sign In as ${uiState.loginTabRole.displayName}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Enter",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
