package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceStatus
import com.example.model.CourseItem
import com.example.model.ScholarshipItem
import com.example.state.EduSmartUiState
import com.example.ui.components.ScholarshipCard
import com.example.ui.components.ScholarshipViewMode
import com.example.ui.theme.*

enum class TeacherActiveModal {
    NONE,
    ATTENDANCE,
    MARKS,
    COURSES,
    SCHOLARSHIPS
}

@Composable
fun TeacherDashboardScreen(
    uiState: EduSmartUiState,
    onToggleAttendance: (Int) -> Unit,
    onSubmitAttendance: () -> Unit,
    onSaveMarks: () -> Unit,
    onUpdateStudentMarks: (Int, Int, Int, Int) -> Unit = { _, _, _, _ -> },
    onAdjustStudentScore: (Int, String, Int) -> Unit = { _, _, _ -> },
    onApplyBonusMarks: () -> Unit = {},
    onEnrollCourse: (String) -> Unit,
    onApproveScholarship: (String) -> Unit,
    onActionToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var activeModal by remember { mutableStateOf(TeacherActiveModal.NONE) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Teacher Profile Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceCard
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    SurfaceCard,
                                    SurfaceCardElevated
                                )
                            )
                        )
                        .border(1.dp, IndigoLight.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(IndigoPrimary, IndigoDark)
                                        )
                                    )
                                    .border(2.dp, IndigoLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "RM",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Prof. Robert Miller",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "Dept. of Computer Science & Eng.",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Faculty ID: TCH-8921 • Grade 10-A Mentor",
                                    fontSize = 11.sp,
                                    color = IndigoLight
                                )
                            }
                        }

                        HorizontalDivider(color = SurfaceCardBorder)

                        // Quick Stat Highlights
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickStatItem(label = "Total Students", value = "42 Enrolled", icon = Icons.Default.Groups)
                            QuickStatItem(label = "Today's Attendance", value = "94.2%", icon = Icons.Default.FactCheck)
                            QuickStatItem(label = "Status", value = "On Duty", icon = Icons.Default.Verified, isSuccess = true)
                        }
                    }
                }
            }
        }

        // Graphical Weekly Attendance Trend Chart (Recharts-style Area Visualization)
        item {
            com.example.ui.components.AttendanceTrendChartCard(
                title = "Class Weekly Attendance Curve",
                subtitle = "Mon - Fri Trend • Grade 10-A Aggregate",
                dataPoints = uiState.teacherAttendanceTrend,
                thresholdPercentage = 75f,
                primaryColor = IndigoLight,
                secondaryColor = SkyInfo,
                isTeacherView = true,
                modifier = Modifier.testTag("teacher_attendance_trend_chart")
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Faculty Action Modules",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "4 Active Portals",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Interactive Grid of 4 Action Modules
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeacherModuleCard(
                        title = "Attendance Tracker",
                        subtitle = "Roll call & live class logs",
                        icon = Icons.Default.ChecklistRtl,
                        badge = "Today: 38/40",
                        gradientColors = listOf(IndigoPrimary, IndigoSecondary),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeModal = TeacherActiveModal.ATTENDANCE
                            onActionToast("Opened Class Attendance Tracker")
                        },
                        testTag = "module_attendance"
                    )

                    TeacherModuleCard(
                        title = "Mark Entry Sheet",
                        subtitle = "Semester grading & GPA",
                        icon = Icons.Default.AutoStories,
                        badge = "Mid-Term",
                        gradientColors = listOf(SkyInfo, IndigoPrimary),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeModal = TeacherActiveModal.MARKS
                            onActionToast("Opened Student Mark Entry Sheet")
                        },
                        testTag = "module_marks"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeacherModuleCard(
                        title = "Govt. Free Courses",
                        subtitle = "Skill programs & NPTEL",
                        icon = Icons.Default.Stars,
                        badge = "4 Certified",
                        gradientColors = listOf(EmeraldSuccess, EmeraldDark),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeModal = TeacherActiveModal.COURSES
                            onActionToast("Opened Government Certified Free Courses")
                        },
                        testTag = "module_courses"
                    )

                    TeacherModuleCard(
                        title = "Scholarship Manager",
                        subtitle = "Verification & Approval",
                        icon = Icons.Default.AccountBalance,
                        badge = "3 Pending",
                        gradientColors = listOf(AmberWarning, PurpleAccent),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            activeModal = TeacherActiveModal.SCHOLARSHIPS
                            onActionToast("Opened Scholarship Approval Manager")
                        },
                        testTag = "module_scholarships"
                    )
                }
            }
        }

        // Recent Class Notice Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Department Announcement",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Mid-Term assessment submissions for Computer Science 10-A close Friday at 5:00 PM. Please verify student attendance quotas.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }

    // Modal Sheets for Modules
    when (activeModal) {
        TeacherActiveModal.ATTENDANCE -> {
            AttendanceModalSheet(
                records = uiState.teacherAttendanceList,
                onToggle = onToggleAttendance,
                onSubmit = {
                    onSubmitAttendance()
                    activeModal = TeacherActiveModal.NONE
                },
                onDismiss = { activeModal = TeacherActiveModal.NONE }
            )
        }
        TeacherActiveModal.MARKS -> {
            MarksModalSheet(
                records = uiState.markRecords,
                onUpdateMarks = onUpdateStudentMarks,
                onAdjustScore = onAdjustStudentScore,
                onApplyBonus = onApplyBonusMarks,
                onSave = {
                    onSaveMarks()
                    activeModal = TeacherActiveModal.NONE
                },
                onDismiss = { activeModal = TeacherActiveModal.NONE }
            )
        }
        TeacherActiveModal.COURSES -> {
            CoursesModalSheet(
                courses = uiState.freeCourses,
                onEnroll = onEnrollCourse,
                onDismiss = { activeModal = TeacherActiveModal.NONE }
            )
        }
        TeacherActiveModal.SCHOLARSHIPS -> {
            ScholarshipsModalSheet(
                scholarships = uiState.scholarships,
                onApprove = onApproveScholarship,
                onDismiss = { activeModal = TeacherActiveModal.NONE }
            )
        }
        TeacherActiveModal.NONE -> {}
    }
}

@Composable
fun QuickStatItem(label: String, value: String, icon: ImageVector, isSuccess: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSuccess) EmeraldLight else IndigoLight,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(text = label, fontSize = 10.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) EmeraldLight else TextPrimary
            )
        }
    }
}

@Composable
fun TeacherModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(140.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(gradientColors.first().copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradientColors.first()
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Attendance Bottom Sheet / Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceModalSheet(
    records: List<com.example.model.StudentAttendanceRecord>,
    onToggle: (Int) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCardElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Attendance Tracker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Grade 10-A • Tap student status to cycle",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            LazyColumn(
                modifier = Modifier.height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records) { student ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(student.rollNo) }
                    ) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "#${student.rollNo}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Text(
                                    text = student.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (student.status) {
                                            AttendanceStatus.PRESENT -> EmeraldSuccess.copy(alpha = 0.2f)
                                            AttendanceStatus.ABSENT -> RoseError.copy(alpha = 0.2f)
                                            AttendanceStatus.LATE -> AmberWarning.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = student.status.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (student.status) {
                                        AttendanceStatus.PRESENT -> EmeraldLight
                                        AttendanceStatus.ABSENT -> RoseError
                                        AttendanceStatus.LATE -> AmberWarning
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit & Sync Attendance", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Mark Entry & Modification Modal Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksModalSheet(
    records: List<com.example.model.StudentMarkRecord>,
    onUpdateMarks: (Int, Int, Int, Int) -> Unit,
    onAdjustScore: (Int, String, Int) -> Unit,
    onApplyBonus: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    var selectedSubject by remember { mutableStateOf("Data Structures & Algorithms (CS301)") }
    var selectedComponent by remember { mutableStateOf("all") } // "all", "internal", "midTerm", "assignment"
    var studentToEdit by remember { mutableStateOf<com.example.model.StudentMarkRecord?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRecords = records.filter {
        it.studentName.contains(searchQuery, ignoreCase = true) || it.rollNo.toString().contains(searchQuery)
    }

    val classAverage = if (records.isNotEmpty()) records.map { it.totalScore }.average().toInt() else 0
    val topScore = records.maxOfOrNull { it.totalScore } ?: 0
    val passCount = records.count { it.totalScore >= 50 }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceCardElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.textMuted) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Grade,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Student Marks Management",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "$selectedSubject • Edit & Grade",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceCard)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = colors.textMuted, modifier = Modifier.size(18.dp))
                }
            }

            // Quick Class Statistics Bar & Bonus Action
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CLASS AVG", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.textMuted)
                            Text("$classAverage/100", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IndigoLight)
                        }
                        Column {
                            Text("HIGHEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.textMuted)
                            Text("$topScore/100", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                        Column {
                            Text("PASSING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = colors.textMuted)
                            Text("$passCount/${records.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SkyInfo)
                        }
                    }

                    Button(
                        onClick = onApplyBonus,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("apply_bonus_marks_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+2 Bonus", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                    }
                }
            }

            // Component Quick-Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "all" to "All Scores",
                    "internal" to "Internal /30",
                    "midTerm" to "Mid-Term /50",
                    "assignment" to "Lab/Assgn /20"
                ).forEach { (key, label) ->
                    val isSelected = selectedComponent == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) IndigoPrimary else colors.surfaceCard)
                            .border(1.dp, if (isSelected) IndigoLight else colors.surfaceCardBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedComponent = key }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else colors.textSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            // Search Bar & Instruction
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search student by name or roll no...", fontSize = 12.sp, color = colors.textMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = colors.textMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = colors.surfaceCardBorder,
                    focusedContainerColor = colors.surfaceCard,
                    unfocusedContainerColor = colors.surfaceCard,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            )

            // Student Mark Records List with Interactive Editing
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRecords) { student ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.surfaceCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mark_row_${student.rollNo}")
                    ) {
                        Column(
                            modifier = Modifier
                                .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${student.rollNo}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IndigoLight
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = student.studentName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "Int: ${student.internalScore}/30 • Exam: ${student.midTermScore}/50 • Lab: ${student.assignmentScore}/20",
                                            fontSize = 11.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${student.totalScore}/100",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = colors.textPrimary
                                        )
                                        val gradeColor = when (student.grade) {
                                            "A+", "A" -> EmeraldLight
                                            "B" -> SkyInfo
                                            "C" -> AmberWarning
                                            else -> RoseError
                                        }
                                        Text(
                                            text = "Grade ${student.grade}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = gradeColor
                                        )
                                    }

                                    IconButton(
                                        onClick = { studentToEdit = student },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(IndigoPrimary.copy(alpha = 0.15f))
                                            .border(1.dp, IndigoLight.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .testTag("edit_marks_btn_${student.rollNo}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit ${student.studentName} marks",
                                            tint = IndigoLight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Quick Adjust Buttons (+ / -) for Quick Tweak
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.surfaceCardElevated)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val activeAdjustName = when (selectedComponent) {
                                    "internal" -> "Internal (${student.internalScore}/30)"
                                    "midTerm" -> "Mid-Term (${student.midTermScore}/50)"
                                    "assignment" -> "Lab/Assgn (${student.assignmentScore}/20)"
                                    else -> "Quick Tweak (Internal ${student.internalScore}/30)"
                                }
                                val activeComponentKey = if (selectedComponent == "all") "internal" else selectedComponent

                                Text(
                                    text = activeAdjustName,
                                    fontSize = 11.sp,
                                    color = colors.textMuted,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Decrement Button
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(RoseError.copy(alpha = 0.15f))
                                            .clickable { onAdjustScore(student.rollNo, activeComponentKey, -1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = RoseError, modifier = Modifier.size(14.dp))
                                    }

                                    // Increment Button
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldSuccess.copy(alpha = 0.2f))
                                            .clickable { onAdjustScore(student.rollNo, activeComponentKey, 1) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = EmeraldLight, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Save & Commit Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_teacher_marks_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save & Sync Marks to University Portal", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }

    // Detailed Mark Editor Dialog
    if (studentToEdit != null) {
        val student = studentToEdit!!
        EditStudentMarksDialog(
            student = student,
            onDismiss = { studentToEdit = null },
            onConfirm = { internal, midTerm, assignment ->
                onUpdateMarks(student.rollNo, internal, midTerm, assignment)
                studentToEdit = null
            }
        )
    }
}

// Interactive Detailed Dialog for Editing a Student's Marks
@Composable
fun EditStudentMarksDialog(
    student: com.example.model.StudentMarkRecord,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int) -> Unit
) {
    val colors = AppTheme.colors

    var internalText by remember { mutableStateOf(student.internalScore.toString()) }
    var midTermText by remember { mutableStateOf(student.midTermScore.toString()) }
    var assignmentText by remember { mutableStateOf(student.assignmentScore.toString()) }

    val internalVal = (internalText.toIntOrNull() ?: 0).coerceIn(0, 30)
    val midTermVal = (midTermText.toIntOrNull() ?: 0).coerceIn(0, 50)
    val assignmentVal = (assignmentText.toIntOrNull() ?: 0).coerceIn(0, 20)

    val previewTotal = internalVal + midTermVal + assignmentVal
    val previewGrade = when {
        previewTotal >= 90 -> "A+"
        previewTotal >= 80 -> "A"
        previewTotal >= 70 -> "B"
        previewTotal >= 60 -> "C"
        previewTotal >= 50 -> "D"
        else -> "F"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceCardElevated,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = IndigoLight)
                Column {
                    Text(
                        text = "Edit Marks: ${student.studentName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Roll Number #${student.rollNo}",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Live Total & Grade Preview Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colors.surfaceCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("COMPUTED TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.textMuted)
                            Text("$previewTotal / 100", fontSize = 18.sp, fontWeight = FontWeight.Black, color = IndigoLight)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (previewGrade) {
                                        "A+", "A" -> EmeraldSuccess.copy(alpha = 0.2f)
                                        "B" -> SkyInfo.copy(alpha = 0.2f)
                                        "C" -> AmberWarning.copy(alpha = 0.2f)
                                        else -> RoseError.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Grade $previewGrade",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (previewGrade) {
                                    "A+", "A" -> EmeraldLight
                                    "B" -> SkyInfo
                                    "C" -> AmberWarning
                                    else -> RoseError
                                }
                            )
                        }
                    }
                }

                // 1. Internal Assessment Score (/30)
                ScoreInputField(
                    label = "Internal Assessment",
                    maxScore = 30,
                    value = internalText,
                    onValueChange = { internalText = it },
                    onIncrement = { internalText = ((internalText.toIntOrNull() ?: 0) + 1).coerceIn(0, 30).toString() },
                    onDecrement = { internalText = ((internalText.toIntOrNull() ?: 0) - 1).coerceIn(0, 30).toString() }
                )

                // 2. Mid-Term Theory Exam Score (/50)
                ScoreInputField(
                    label = "Mid-Term Examination",
                    maxScore = 50,
                    value = midTermText,
                    onValueChange = { midTermText = it },
                    onIncrement = { midTermText = ((midTermText.toIntOrNull() ?: 0) + 1).coerceIn(0, 50).toString() },
                    onDecrement = { midTermText = ((midTermText.toIntOrNull() ?: 0) - 1).coerceIn(0, 50).toString() }
                )

                // 3. Lab / Assignments (/20)
                ScoreInputField(
                    label = "Lab & Assignments",
                    maxScore = 20,
                    value = assignmentText,
                    onValueChange = { assignmentText = it },
                    onIncrement = { assignmentText = ((assignmentText.toIntOrNull() ?: 0) + 1).coerceIn(0, 20).toString() },
                    onDecrement = { assignmentText = ((assignmentText.toIntOrNull() ?: 0) - 1).coerceIn(0, 20).toString() }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(internalVal, midTermVal, assignmentVal) },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_update_marks_btn")
            ) {
                Text("Update Marks", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.surfaceCardBorder)
            ) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}

@Composable
fun ScoreInputField(
    label: String,
    maxScore: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val colors = AppTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
            Text(text = "Max: $maxScore", fontSize = 11.sp, color = colors.textMuted)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Decrement Button
            IconButton(
                onClick = onDecrement,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceCard)
                    .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", tint = RoseError, modifier = Modifier.size(18.dp))
            }

            // Numeric Input Field
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        val num = newValue.toIntOrNull()
                        if (num == null || num <= maxScore) {
                            onValueChange(newValue)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoPrimary,
                    unfocusedBorderColor = colors.surfaceCardBorder,
                    focusedContainerColor = colors.surfaceCard,
                    unfocusedContainerColor = colors.surfaceCard,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )

            // Increment Button
            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceCard)
                    .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Plus", tint = EmeraldLight, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// Courses Modal Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesModalSheet(
    courses: List<CourseItem>,
    onEnroll: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCardElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Government Free Courses",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "PMKVY, Swayam & AICTE Certified Modules",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(courses) { course ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = course.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "${course.provider} • ${course.duration}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            FilledTonalButton(
                                onClick = { onEnroll(course.id) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (course.isEnrolled) EmeraldSuccess.copy(alpha = 0.2f) else IndigoPrimary.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (course.isEnrolled) "Enrolled ✓" else "Recommend",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (course.isEnrolled) EmeraldLight else IndigoLight
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Scholarships Modal Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipsModalSheet(
    scholarships: List<ScholarshipItem>,
    onApprove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCardElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = IndigoLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Scholarship Approvals",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${scholarships.size} APPLICATIONS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoLight
                                )
                            }
                        }
                        Text(
                            text = "State Welfare Grants • Faculty Verification Queue",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 440.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scholarships) { item ->
                    ScholarshipCard(
                        scholarship = item,
                        viewMode = ScholarshipViewMode.TEACHER,
                        onAction = onApprove
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
