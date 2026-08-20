package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.state.EduSmartViewModel
import com.example.ui.components.EduSmartTopBar
import com.example.ui.components.ToastNotificationHost
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EduSmartViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            EduSmartTheme(darkTheme = uiState.isDarkMode) {
                EduSmartApp(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
fun EduSmartApp(
    viewModel: EduSmartViewModel = viewModel(),
    uiState: com.example.state.EduSmartUiState = viewModel.uiState.collectAsStateWithLifecycle().value
) {
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!uiState.isLoggedIn) {
            LoginScreen(
                uiState = uiState,
                onRoleSelected = viewModel::setLoginTabRole,
                onMobileChange = viewModel::updateMobileInput,
                onOtpChange = viewModel::updateOtpInput,
                onStudentIdChange = viewModel::updateStudentIdInput,
                onPasswordChange = viewModel::updatePasswordInput,
                onBypassLogin = viewModel::loginWithBypass,
                onToggleDarkMode = viewModel::toggleDarkMode,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = colors.background,
                topBar = {
                    EduSmartTopBar(
                        currentRole = uiState.currentRole,
                        isDarkMode = uiState.isDarkMode,
                        onToggleDarkMode = viewModel::toggleDarkMode,
                        onLogout = viewModel::logout
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (uiState.currentRole) {
                        UserRole.TEACHER -> {
                            TeacherDashboardScreen(
                                uiState = uiState,
                                onToggleAttendance = viewModel::toggleAttendance,
                                onSubmitAttendance = viewModel::submitTeacherAttendance,
                                onSaveMarks = viewModel::saveTeacherMarks,
                                onUpdateStudentMarks = viewModel::updateStudentMarks,
                                onAdjustStudentScore = viewModel::adjustStudentScore,
                                onApplyBonusMarks = viewModel::applyBonusMarksToAll,
                                onEnrollCourse = viewModel::enrollCourse,
                                onApproveScholarship = viewModel::approveScholarship,
                                onActionToast = { viewModel.showToast(it) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        UserRole.PARENT -> {
                            ParentDashboardScreen(
                                uiState = uiState,
                                onParentAction = viewModel::handleParentAction,
                                onApproveScholarship = viewModel::approveScholarship,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        UserRole.STUDENT -> {
                            StudentDashboardScreen(
                                uiState = uiState,
                                onTabSelected = viewModel::setStudentTab,
                                onGrievanceTypeChange = viewModel::setGrievanceType,
                                onGrievanceDescChange = viewModel::setGrievanceDescription,
                                onGrievancePriorityChange = viewModel::setGrievancePriority,
                                onSubmitGrievance = viewModel::submitGrievance,
                                onToggleDocument = viewModel::toggleDocumentUpload,
                                onCourseShortcut = {
                                    viewModel.showToast("Navigated to Government Free Courses Catalog")
                                },
                                onScholarshipShortcut = {
                                    viewModel.showToast("Displaying 5 State Scholarships & Schemes")
                                },
                                onApproveScholarship = viewModel::approveScholarship,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Global Floating Internet-Connected Chatbot in the bottom right corner (Only shown on authenticated dashboards, NOT in login)
        if (uiState.isLoggedIn) {
            com.example.ui.components.EduSmartFloatingChatbot(
                uiState = uiState,
                onToggleChatbot = viewModel::toggleChatbot,
                onSendMessage = { prompt, onDone -> viewModel.sendChatMessage(prompt, onDone) },
                onInputChange = viewModel::updateChatbotInput,
                onToggleAutoVoice = viewModel::toggleAutoVoiceResponse,
                onSetVoiceListening = viewModel::setVoiceListening,
                onSetVoiceSpeaking = viewModel::setVoiceSpeaking,
                onClearChat = viewModel::clearChatHistory,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Global Floating Toast Notification at the Top of the Viewport
        ToastNotificationHost(
            toast = uiState.currentToast,
            onDismiss = viewModel::dismissToast,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}
