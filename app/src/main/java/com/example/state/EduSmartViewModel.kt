package com.example.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class StudentTab {
    CORE_MODULES,
    STUDENT_PROFILE
}

enum class ToastType {
    SUCCESS,
    INFO,
    WARNING
}

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: ToastType = ToastType.SUCCESS
)

data class EduSmartUiState(
    val isDarkMode: Boolean = true,
    val isLoggedIn: Boolean = false,
    val currentRole: UserRole = UserRole.STUDENT,
    val loginTabRole: UserRole = UserRole.STUDENT,
    val mobileNumberInput: String = "9876543210",
    val otpInput: String = "408192",
    val studentIdInput: String = "EDU-2026-STU881",
    val passwordInput: String = "••••••••",
    val studentActiveTab: StudentTab = StudentTab.CORE_MODULES,
    
    // Grievance Redressal State
    val selectedGrievanceType: String = "General Grievance",
    val grievanceDescription: String = "",
    val grievancePriority: String = "Normal",
    val grievanceTickets: List<GrievanceTicket> = listOf(
        GrievanceTicket(
            id = "GRV-2026-092",
            issueType = "Infrastructure & Lab",
            priority = "Normal",
            description = "High-speed Ethernet socket at Lab-3 workstation 14 is loose and disconnecting.",
            timestamp = "Yesterday, 3:45 PM",
            status = "Resolved"
        ),
        GrievanceTicket(
            id = "GRV-2026-104",
            issueType = "Examination Dispute",
            priority = "High",
            description = "Mid-term Physics paper total was computed as 34 instead of 44. Re-evaluation requested.",
            timestamp = "Aug 16, 11:20 AM",
            status = "Under Review"
        )
    ),

    // Photocopy Documents
    val documents: List<DocumentItem> = listOf(
        DocumentItem(
            id = "income_cert",
            title = "Income Certificate",
            subtitle = "Issued by Tahsildar / Revenue Dept (Max 2MB)",
            isUploaded = true,
            fileName = "Income_Certificate_2026.pdf",
            fileSize = "1.4 MB",
            uploadDate = "Verified on Aug 05"
        ),
        DocumentItem(
            id = "community_cert",
            title = "Community Certificate",
            subtitle = "Official Caste / Community Proof (Max 2MB)",
            isUploaded = true,
            fileName = "Community_Proof_OBC.pdf",
            fileSize = "890 KB",
            uploadDate = "Verified on Aug 05"
        ),
        DocumentItem(
            id = "aadhaar_card",
            title = "Aadhaar Card",
            subtitle = "Front & Back Government ID scan",
            isUploaded = false,
            fileName = null,
            fileSize = null,
            uploadDate = null
        ),
        DocumentItem(
            id = "nativity_cert",
            title = "Nativity Certificate",
            subtitle = "State Domicile / Residence Certificate",
            isUploaded = false,
            fileName = null,
            fileSize = null,
            uploadDate = null
        )
    ),

    // Teacher Attendance
    val teacherAttendanceList: List<StudentAttendanceRecord> = listOf(
        StudentAttendanceRecord(1, "Alex Miller", AttendanceStatus.PRESENT),
        StudentAttendanceRecord(2, "Sophia Chen", AttendanceStatus.PRESENT),
        StudentAttendanceRecord(3, "Marcus Vance", AttendanceStatus.PRESENT),
        StudentAttendanceRecord(4, "Emma Watson", AttendanceStatus.ABSENT),
        StudentAttendanceRecord(5, "David Patel", AttendanceStatus.PRESENT),
        StudentAttendanceRecord(6, "Olivia Ross", AttendanceStatus.LATE),
        StudentAttendanceRecord(7, "Lucas Wright", AttendanceStatus.PRESENT),
        StudentAttendanceRecord(8, "Ava Martinez", AttendanceStatus.PRESENT)
    ),

    // Teacher Mark Entry
    val markRecords: List<StudentMarkRecord> = listOf(
        StudentMarkRecord("Alex Miller", 1, 28, 45, 18),
        StudentMarkRecord("Sophia Chen", 2, 30, 48, 20),
        StudentMarkRecord("Marcus Vance", 3, 24, 38, 15),
        StudentMarkRecord("Emma Watson", 4, 26, 42, 17),
        StudentMarkRecord("David Patel", 5, 29, 46, 19)
    ),

    // Free Courses
    val freeCourses: List<CourseItem> = listOf(
        CourseItem(
            id = "c1",
            title = "Cloud & Distributed Systems",
            provider = "Govt. NPTEL / Swayam",
            duration = "8 Weeks • Certified",
            tag = "100% Free",
            isEnrolled = true
        ),
        CourseItem(
            id = "c2",
            title = "Cybersecurity & Ethical Defense",
            provider = "PMKVY National Skill",
            duration = "12 Weeks • Labs Included",
            tag = "Govt. Accredited",
            isEnrolled = false
        ),
        CourseItem(
            id = "c3",
            title = "AI Prompt Engineering & Ethics",
            provider = "Digital India Initiative",
            duration = "6 Weeks • Self-Paced",
            tag = "Govt. Funded",
            isEnrolled = false
        ),
        CourseItem(
            id = "c4",
            title = "Embedded IoT & Robotics",
            provider = "AICTE Free Skill Portal",
            duration = "10 Weeks • Hands-on",
            tag = "Free Certificate",
            isEnrolled = false
        )
    ),

    // Scholarships
    val scholarships: List<ScholarshipItem> = listOf(
        ScholarshipItem(
            id = "sch_pudhumai_penn",
            title = "Moovalur Ramamirtham Ammaiyar Pudhumai Penn Scheme",
            amount = "₹1,000 / Month (₹12,000/Yr)",
            applicant = "Sophia Chen (Roll #2) • B.Tech AI & DS",
            status = "Pending Verification",
            date = "Aug 18, 2026",
            tag = "Girls",
            description = "Higher education financial assistance for female students from Tamil Nadu government schools."
        ),
        ScholarshipItem(
            id = "sch_tamil_pudhalvan",
            title = "Tamil Pudhalvan Higher Education Scheme",
            amount = "₹1,000 / Month (₹12,000/Yr)",
            applicant = "Alex Miller (Roll #1) • B.Tech CSE",
            status = "Approved by Faculty",
            date = "Aug 16, 2026",
            tag = "Boys",
            description = "Higher education financial assistance for male students from Tamil Nadu government schools."
        ),
        ScholarshipItem(
            id = "sch_post_matric_sc_st",
            title = "Post-Matric Scholarship Scheme for SC / ST / SCC Students",
            amount = "₹50,000 / Year (100% Tuition Fee)",
            applicant = "Marcus Vance (Roll #3) • B.Tech IT",
            status = "Pending Verification",
            date = "Aug 19, 2026",
            tag = "SC/ST/SCC",
            description = "Full tuition fee waiver, examination fee exemption, and maintenance grant for SC / ST / SCC scholars."
        ),
        ScholarshipItem(
            id = "sch_post_matric_bc_mbc",
            title = "Post-Matric Scholarship Scheme for BC / MBC / DNC Students",
            amount = "₹30,000 / Year (Fee Assistance)",
            applicant = "David Patel (Roll #5) • B.Tech ECE",
            status = "Approved by Faculty",
            date = "Aug 15, 2026",
            tag = "BC/MBC/DNC",
            description = "State post-matric tuition fee subsidy, special fees, and hostel assistance for BC / MBC / DNC scholars."
        ),
        ScholarshipItem(
            id = "sch_evr_nagammai",
            title = "Periyar E.V.R. Nagammai Free Higher Education Scheme",
            amount = "₹25,000 / Year (Tuition Exemption)",
            applicant = "Emma Watson (Roll #4) • B.Tech Cyber Security",
            status = "Under Verification",
            date = "Aug 17, 2026",
            tag = "Girls",
            description = "Tuition fee exemption and financial aid scheme for women pursuing higher education in Tamil Nadu."
        )
    ),

    // Teacher Attendance Trend (Recharts-Style Data)
    val teacherAttendanceTrend: List<com.example.ui.components.AttendanceDataPoint> = listOf(
        com.example.ui.components.AttendanceDataPoint("Mon", 92.5f, 37, 40, "Monday, Aug 15"),
        com.example.ui.components.AttendanceDataPoint("Tue", 95.0f, 38, 40, "Tuesday, Aug 16"),
        com.example.ui.components.AttendanceDataPoint("Wed", 87.5f, 35, 40, "Wednesday, Aug 17"),
        com.example.ui.components.AttendanceDataPoint("Thu", 97.5f, 39, 40, "Thursday, Aug 18"),
        com.example.ui.components.AttendanceDataPoint("Fri", 94.2f, 38, 40, "Friday, Aug 19")
    ),

    // Parent Student Attendance Trend (Recharts-Style Data)
    val parentStudentAttendanceTrend: List<com.example.ui.components.AttendanceDataPoint> = listOf(
        com.example.ui.components.AttendanceDataPoint("W1", 90.0f, 18, 20, "Week 1 (July 18 - 22)"),
        com.example.ui.components.AttendanceDataPoint("W2", 95.0f, 19, 20, "Week 2 (July 25 - 29)"),
        com.example.ui.components.AttendanceDataPoint("W3", 85.0f, 17, 20, "Week 3 (Aug 01 - 05)"),
        com.example.ui.components.AttendanceDataPoint("W4", 95.0f, 19, 20, "Week 4 (Aug 08 - 12)"),
        com.example.ui.components.AttendanceDataPoint("W5", 91.5f, 146, 160, "Week 5 (Aug 15 - 19)")
    ),

    // Internet-Connected Chatbot State
    val isChatbotOpen: Boolean = false,
    val isChatbotThinking: Boolean = false,
    val chatbotInputText: String = "",
    val isAutoVoiceResponseEnabled: Boolean = true,
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val lastSpokenMessageId: String? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = MessageSender.BOT,
            text = "👋 Hello! I am **EduAI**, your voice & internet-connected academic assistant. Ask me anything by typing or tapping the 🎙️ **Microphone** button!",
            isWebGrounded = true
        )
    ),

    // Active Toast
    val currentToast: ToastMessage? = null
)

class EduSmartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EduSmartUiState())
    val uiState: StateFlow<EduSmartUiState> = _uiState.asStateFlow()

    private var toastJob: Job? = null

    fun showToast(message: String, type: ToastType = ToastType.SUCCESS) {
        toastJob?.cancel()
        _uiState.update { it.copy(currentToast = ToastMessage(message = message, type = type)) }
        toastJob = viewModelScope.launch {
            delay(3200)
            _uiState.update { it.copy(currentToast = null) }
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(currentToast = null) }
    }

    fun setLoginTabRole(role: UserRole) {
        _uiState.update { it.copy(loginTabRole = role) }
    }

    fun updateMobileInput(value: String) {
        _uiState.update { it.copy(mobileNumberInput = value) }
    }

    fun updateOtpInput(value: String) {
        _uiState.update { it.copy(otpInput = value) }
    }

    fun updateStudentIdInput(value: String) {
        _uiState.update { it.copy(studentIdInput = value) }
    }

    fun updatePasswordInput(value: String) {
        _uiState.update { it.copy(passwordInput = value) }
    }

    // UNIVERSAL BYPASS LOGIN LOGIC
    // Reads highlighted role tab and immediately routes into that role's dashboard
    fun loginWithBypass() {
        val activeRole = _uiState.value.loginTabRole
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                currentRole = activeRole
            )
        }
        showToast("Welcome back! Signed in as ${activeRole.displayName} (Universal Bypass Active)")
    }

    fun switchRole(role: UserRole) {
        _uiState.update {
            it.copy(
                currentRole = role,
                loginTabRole = role
            )
        }
        showToast("Switched viewport to ${role.displayName} Dashboard", ToastType.INFO)
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isLoggedIn = false
            )
        }
        showToast("Logged out successfully from EduSmart Portal", ToastType.INFO)
    }

    fun setStudentTab(tab: StudentTab) {
        _uiState.update { it.copy(studentActiveTab = tab) }
    }

    fun setGrievanceType(type: String) {
        _uiState.update { it.copy(selectedGrievanceType = type) }
    }

    fun setGrievanceDescription(desc: String) {
        _uiState.update { it.copy(grievanceDescription = desc) }
    }

    fun setGrievancePriority(priority: String) {
        _uiState.update { it.copy(grievancePriority = priority) }
    }

    fun submitGrievance() {
        val currentState = _uiState.value
        val desc = currentState.grievanceDescription.trim().ifEmpty {
            "Request for academic & administrative review regarding ${currentState.selectedGrievanceType}."
        }
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        val formattedTime = sdf.format(Date())
        val randomNum = (100..999).random()
        val newTicket = GrievanceTicket(
            id = "GRV-2026-$randomNum",
            issueType = currentState.selectedGrievanceType,
            priority = currentState.grievancePriority,
            description = desc,
            timestamp = "Today, $formattedTime",
            status = "Logged & Dispatched"
        )

        _uiState.update {
            it.copy(
                grievanceTickets = listOf(newTicket) + it.grievanceTickets,
                grievanceDescription = ""
            )
        }
        showToast("Grievance #${newTicket.id} logged and forwarded to Cell!")
    }

    fun toggleDocumentUpload(documentId: String) {
        var toggledTitle = ""
        var isNowUploaded = false

        _uiState.update { state ->
            val updatedList = state.documents.map { doc ->
                if (doc.id == documentId) {
                    toggledTitle = doc.title
                    val newState = !doc.isUploaded
                    isNowUploaded = newState
                    if (newState) {
                        doc.copy(
                            isUploaded = true,
                            fileName = "${doc.title.replace(" ", "_")}_Verified.pdf",
                            fileSize = "1.2 MB",
                            uploadDate = "Uploaded just now"
                        )
                    } else {
                        doc.copy(
                            isUploaded = false,
                            fileName = null,
                            fileSize = null,
                            uploadDate = null
                        )
                    }
                } else {
                    doc
                }
            }
            state.copy(documents = updatedList)
        }

        if (isNowUploaded) {
            showToast("✓ $toggledTitle attached & verified successfully!")
        } else {
            showToast("Removed $toggledTitle attachment", ToastType.INFO)
        }
    }

    fun toggleAttendance(rollNo: Int) {
        _uiState.update { state ->
            val updated = state.teacherAttendanceList.map { student ->
                if (student.rollNo == rollNo) {
                    val nextStatus = when (student.status) {
                        AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
                        AttendanceStatus.ABSENT -> AttendanceStatus.LATE
                        AttendanceStatus.LATE -> AttendanceStatus.PRESENT
                    }
                    student.copy(status = nextStatus)
                } else student
            }
            state.copy(teacherAttendanceList = updated)
        }
    }

    fun submitTeacherAttendance() {
        val presentCount = _uiState.value.teacherAttendanceList.count { it.status == AttendanceStatus.PRESENT }
        val total = _uiState.value.teacherAttendanceList.size
        showToast("Class 10-A Attendance saved! ($presentCount/$total Present)")
    }

    fun updateStudentMarks(
        rollNo: Int,
        internalScore: Int,
        midTermScore: Int,
        assignmentScore: Int
    ) {
        _uiState.update { state ->
            val updated = state.markRecords.map { record ->
                if (record.rollNo == rollNo) {
                    record.copy(
                        internalScore = internalScore.coerceIn(0, 30),
                        midTermScore = midTermScore.coerceIn(0, 50),
                        assignmentScore = assignmentScore.coerceIn(0, 20)
                    )
                } else record
            }
            state.copy(markRecords = updated)
        }
        val updatedStudent = _uiState.value.markRecords.find { it.rollNo == rollNo }
        if (updatedStudent != null) {
            showToast("Updated marks for ${updatedStudent.studentName}: ${updatedStudent.totalScore}/100 (Grade ${updatedStudent.grade})")
        }
    }

    fun adjustStudentScore(rollNo: Int, component: String, delta: Int) {
        _uiState.update { state ->
            val updated = state.markRecords.map { record ->
                if (record.rollNo == rollNo) {
                    when (component) {
                        "internal" -> record.copy(internalScore = (record.internalScore + delta).coerceIn(0, 30))
                        "midTerm" -> record.copy(midTermScore = (record.midTermScore + delta).coerceIn(0, 50))
                        "assignment" -> record.copy(assignmentScore = (record.assignmentScore + delta).coerceIn(0, 20))
                        else -> record
                    }
                } else record
            }
            state.copy(markRecords = updated)
        }
    }

    fun applyBonusMarksToAll(bonus: Int = 2) {
        _uiState.update { state ->
            val updated = state.markRecords.map { record ->
                record.copy(
                    internalScore = (record.internalScore + bonus).coerceIn(0, 30),
                    midTermScore = (record.midTermScore + bonus).coerceIn(0, 50)
                )
            }
            state.copy(markRecords = updated)
        }
        showToast("Awarded +$bonus Bonus/Grace Marks to all students!")
    }

    fun saveTeacherMarks() {
        val totalAvg = if (_uiState.value.markRecords.isNotEmpty()) {
            _uiState.value.markRecords.map { it.totalScore }.average().toInt()
        } else 0
        showToast("✓ All Mid-Term marks published! Class Average: $totalAvg/100")
    }

    fun enrollCourse(courseId: String) {
        _uiState.update { state ->
            val updated = state.freeCourses.map {
                if (it.id == courseId) it.copy(isEnrolled = !it.isEnrolled) else it
            }
            state.copy(freeCourses = updated)
        }
        val course = _uiState.value.freeCourses.find { it.id == courseId }
        if (course?.isEnrolled == true) {
            showToast("Registered for ${course.title} (Govt. Free Scheme)!")
        } else {
            showToast("Updated course preferences", ToastType.INFO)
        }
    }

    fun approveScholarship(schId: String) {
        val target = _uiState.value.scholarships.find { it.id == schId }
        val role = _uiState.value.currentRole
        val approvedStatus = when (role) {
            UserRole.TEACHER -> "Approved by Faculty"
            UserRole.PARENT -> "Approved with Parent Consent"
            UserRole.STUDENT -> "Verified & Submitted"
        }
        _uiState.update { state ->
            val updated = state.scholarships.map {
                if (it.id == schId) it.copy(status = approvedStatus) else it
            }
            state.copy(scholarships = updated)
        }
        val schName = target?.title ?: "Scholarship"
        when (role) {
            UserRole.TEACHER -> showToast("✓ Faculty approved $schName for State Treasury!")
            UserRole.PARENT -> showToast("✓ Parent consent & approval granted for $schName!")
            UserRole.STUDENT -> showToast("✓ Student verification submitted for $schName!")
        }
    }

    fun toggleDarkMode() {
        val nextMode = !_uiState.value.isDarkMode
        _uiState.update { it.copy(isDarkMode = nextMode) }
        showToast(
            if (nextMode) "Switched to Dark Mode 🌙" else "Switched to Light Mode ☀️",
            ToastType.INFO
        )
    }

    fun handleParentAction(action: String) {
        when {
            action.startsWith("leave:") -> {
                val details = action.removePrefix("leave:")
                showToast("✓ Leave application sent to Mentor: $details", ToastType.SUCCESS)
            }
            action == "leave" -> showToast("Leave application submitted for Alex Miller")
            action == "report" -> showToast("Official Term Report Card downloaded (PDF)")
            action == "call_mentor" || action == "contact" -> showToast("Calling Mentor Prof. Robert Miller (+91 6382835276)...", ToastType.INFO)
            else -> showToast("Action processed successfully")
        }
    }

    // Chatbot Management
    fun toggleChatbot() {
        _uiState.update { it.copy(isChatbotOpen = !it.isChatbotOpen) }
    }

    fun openChatbot() {
        _uiState.update { it.copy(isChatbotOpen = true) }
    }

    fun closeChatbot() {
        _uiState.update { it.copy(isChatbotOpen = false) }
    }

    fun updateChatbotInput(text: String) {
        _uiState.update { it.copy(chatbotInputText = text) }
    }

    fun toggleAutoVoiceResponse() {
        val next = !_uiState.value.isAutoVoiceResponseEnabled
        _uiState.update { it.copy(isAutoVoiceResponseEnabled = next) }
        showToast(if (next) "Auto Voice Responses Enabled 🔊" else "Voice Responses Muted 🔇", ToastType.INFO)
    }

    fun setVoiceListening(listening: Boolean) {
        _uiState.update { it.copy(isListening = listening) }
    }

    fun setVoiceSpeaking(speaking: Boolean, messageId: String? = null) {
        _uiState.update {
            it.copy(
                isSpeaking = speaking,
                lastSpokenMessageId = if (speaking) messageId else null
            )
        }
    }

    fun sendChatMessage(customPrompt: String? = null, onResponseReceived: ((String) -> Unit)? = null) {
        val promptToSend = customPrompt ?: _uiState.value.chatbotInputText
        val cleanPrompt = promptToSend.trim()
        if (cleanPrompt.isEmpty()) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = cleanPrompt
        )

        _uiState.update { state ->
            state.copy(
                chatMessages = state.chatMessages + userMessage,
                chatbotInputText = "",
                isChatbotThinking = true,
                isChatbotOpen = true
            )
        }

        viewModelScope.launch {
            val result = com.example.data.GeminiService.queryInternet(cleanPrompt)
            val botMessage = ChatMessage(
                sender = MessageSender.BOT,
                text = result.answer,
                isWebGrounded = result.isWebGrounded,
                sourceLinks = result.sourceLinks
            )
            _uiState.update { state ->
                state.copy(
                    chatMessages = state.chatMessages + botMessage,
                    isChatbotThinking = false
                )
            }
            onResponseReceived?.invoke(result.answer)
        }
    }

    fun clearChatHistory() {
        _uiState.update { state ->
            state.copy(
                chatMessages = listOf(
                    ChatMessage(
                        sender = MessageSender.BOT,
                        text = "Chat history cleared. What else can I look up for you on the internet?",
                        isWebGrounded = true
                    )
                )
            )
        }
        showToast("Chat history reset", ToastType.INFO)
    }
}

