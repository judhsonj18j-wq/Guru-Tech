package com.example.model

enum class UserRole(val displayName: String, val badge: String) {
    TEACHER("Teacher", "Faculty"),
    PARENT("Parent", "Guardian"),
    STUDENT("Student", "Enrolled")
}

data class GrievanceTicket(
    val id: String,
    val issueType: String,
    val priority: String,
    val description: String,
    val timestamp: String,
    val status: String
)

data class DocumentItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isUploaded: Boolean = false,
    val fileName: String? = null,
    val fileSize: String? = null,
    val uploadDate: String? = null
)

data class CourseItem(
    val id: String,
    val title: String,
    val provider: String,
    val duration: String,
    val tag: String,
    val isEnrolled: Boolean = false
)

data class ScholarshipItem(
    val id: String,
    val title: String,
    val amount: String,
    val applicant: String,
    val status: String,
    val date: String,
    val tag: String = "",
    val description: String = ""
)

data class StudentAttendanceRecord(
    val rollNo: Int,
    val name: String,
    val status: AttendanceStatus
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE
}

data class StudentMarkRecord(
    val studentName: String,
    val rollNo: Int,
    val internalScore: Int,
    val midTermScore: Int,
    val assignmentScore: Int
) {
    val totalScore: Int get() = internalScore + midTermScore + assignmentScore
    val grade: String
        get() = when {
            totalScore >= 90 -> "A+"
            totalScore >= 80 -> "A"
            totalScore >= 70 -> "B"
            totalScore >= 60 -> "C"
            totalScore >= 50 -> "D"
            else -> "F"
        }
}

enum class MessageSender {
    USER,
    BOT
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: String = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
    val isWebGrounded: Boolean = false,
    val sourceLinks: List<String> = emptyList()
)

