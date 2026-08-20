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
import com.example.model.DocumentItem
import com.example.model.GrievanceTicket
import com.example.state.EduSmartUiState
import com.example.state.StudentTab
import com.example.ui.components.AttendanceTrendChartCard
import com.example.ui.components.ScholarshipCard
import com.example.ui.components.ScholarshipViewMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    uiState: EduSmartUiState,
    onTabSelected: (StudentTab) -> Unit,
    onGrievanceTypeChange: (String) -> Unit,
    onGrievanceDescChange: (String) -> Unit,
    onGrievancePriorityChange: (String) -> Unit,
    onSubmitGrievance: () -> Unit,
    onToggleDocument: (String) -> Unit,
    onCourseShortcut: () -> Unit,
    onScholarshipShortcut: () -> Unit,
    onApproveScholarship: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var isGrievanceDropdownOpen by remember { mutableStateOf(false) }
    var isScholarshipSheetOpen by remember { mutableStateOf(false) }
    val grievanceOptions = listOf(
        "General Grievance",
        "Complain Against Teacher",
        "Infrastructure & Lab Issue",
        "Examination & Grading Dispute",
        "Scholarship & Fee Query",
        "Hostel & Canteen Facility"
    )

    // Expandable card cluster state
    var isFatherExpanded by remember { mutableStateOf(true) }
    var isMotherExpanded by remember { mutableStateOf(false) }
    var isGuardianExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Dual-Tab Workspace Segmented Toggle at the Top
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            color = colors.surfaceCardElevated,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 1: Core Modules
                val isCoreSelected = uiState.studentActiveTab == StudentTab.CORE_MODULES
                val coreBgModifier = if (isCoreSelected) {
                    Modifier.background(Brush.horizontalGradient(listOf(IndigoPrimary, IndigoSecondary)))
                } else {
                    Modifier.background(Color.Transparent)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .then(coreBgModifier)
                        .clickable { onTabSelected(StudentTab.CORE_MODULES) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_core_modules"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = null,
                            tint = if (isCoreSelected) Color.White else colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Core Modules",
                            fontSize = 13.sp,
                            fontWeight = if (isCoreSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCoreSelected) Color.White else colors.textSecondary
                        )
                    }
                }

                // Tab 2: Student Profile
                val isProfileSelected = uiState.studentActiveTab == StudentTab.STUDENT_PROFILE
                val profileBgModifier = if (isProfileSelected) {
                    Modifier.background(Brush.horizontalGradient(listOf(EmeraldSuccess, EmeraldDark)))
                } else {
                    Modifier.background(Color.Transparent)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .then(profileBgModifier)
                        .clickable { onTabSelected(StudentTab.STUDENT_PROFILE) }
                        .padding(vertical = 10.dp)
                        .testTag("tab_student_profile"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                            tint = if (isProfileSelected) Color.White else colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Student Profile",
                            fontSize = 13.sp,
                            fontWeight = if (isProfileSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isProfileSelected) Color.White else colors.textSecondary
                        )
                    }
                }
            }
        }

        // Animated Tab Content
        AnimatedContent(
            targetState = uiState.studentActiveTab,
            transitionSpec = {
                if (targetState == StudentTab.STUDENT_PROFILE) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "StudentWorkspaceAnim"
        ) { currentTab ->
            when (currentTab) {
                // ==================== TAB A: CORE MODULES ====================
                StudentTab.CORE_MODULES -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Attendance Trend Chart Card for Student (Matching Parent View)
                        item {
                            AttendanceTrendChartCard(
                                title = "My Attendance Curve",
                                subtitle = "5-Week Consistency • 146/160 Days (Alex Miller)",
                                dataPoints = uiState.parentStudentAttendanceTrend,
                                thresholdPercentage = 75f,
                                primaryColor = EmeraldSuccess,
                                secondaryColor = EmeraldLight,
                                isTeacherView = false,
                                modifier = Modifier.testTag("student_attendance_trend_chart")
                            )
                        }

                        // Quick Action Shortcut Links (Gov Free Courses & Scholarships)
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(14.dp))
                                        .clickable { onCourseShortcut() }
                                        .testTag("shortcut_gov_courses"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = colors.surfaceCard
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, IndigoLight.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(IndigoPrimary.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.School, contentDescription = null, tint = IndigoLight, modifier = Modifier.size(18.dp))
                                                }
                                                Text(text = "FREE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                            }
                                            Text(text = "Gov Free Courses", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                            Text(text = "AICTE & NPTEL Catalog", fontSize = 11.sp, color = colors.textSecondary)
                                        }
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(14.dp))
                                        .clickable { 
                                            isScholarshipSheetOpen = true
                                            onScholarshipShortcut()
                                        }
                                        .testTag("shortcut_scholarships"),
                                    shape = RoundedCornerShape(14.dp),
                                    color = colors.surfaceCard
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(EmeraldSuccess.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                                }
                                                Text(text = "5 SCHEMES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                            }
                                            Text(text = "State Scholarships", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                            Text(text = "Pudhumai Penn & Grants", fontSize = 11.sp, color = colors.textSecondary)
                                        }
                                    }
                                }
                            }
                        }

                        // Grievance Redressal Section Form
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceCard
                            ) {
                                Column(
                                    modifier = Modifier
                                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReportProblem,
                                            contentDescription = null,
                                            tint = AmberWarning,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Grievance Redressal Cell",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    Text(
                                        text = "Direct student submission to grievance committee. Complaints are logged confidentially.",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 16.sp
                                    )

                                    // Dropdown Selector
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Select Issue Type",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )

                                        ExposedDropdownMenuBox(
                                            expanded = isGrievanceDropdownOpen,
                                            onExpandedChange = { isGrievanceDropdownOpen = !isGrievanceDropdownOpen }
                                        ) {
                                            OutlinedTextField(
                                                value = uiState.selectedGrievanceType,
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGrievanceDropdownOpen) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = IndigoPrimary,
                                                    unfocusedBorderColor = SurfaceCardBorder,
                                                    focusedContainerColor = SurfaceCardElevated,
                                                    unfocusedContainerColor = SurfaceCardElevated,
                                                    focusedTextColor = TextPrimary,
                                                    unfocusedTextColor = TextPrimary
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth()
                                                    .testTag("grievance_type_dropdown")
                                            )

                                            ExposedDropdownMenu(
                                                expanded = isGrievanceDropdownOpen,
                                                onDismissRequest = { isGrievanceDropdownOpen = false },
                                                modifier = Modifier.background(SurfaceCardElevated)
                                            ) {
                                                grievanceOptions.forEach { option ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Text(
                                                                text = option,
                                                                color = if (option == uiState.selectedGrievanceType) IndigoLight else TextPrimary,
                                                                fontWeight = if (option == uiState.selectedGrievanceType) FontWeight.Bold else FontWeight.Normal
                                                            )
                                                        },
                                                        onClick = {
                                                            onGrievanceTypeChange(option)
                                                            isGrievanceDropdownOpen = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Priority Selector Chips
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Urgency / Priority Level",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("Normal", "High", "Urgent").forEach { priority ->
                                                val isSelected = uiState.grievancePriority == priority
                                                val chipColor = when (priority) {
                                                    "Urgent" -> RoseError
                                                    "High" -> AmberWarning
                                                    else -> IndigoLight
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isSelected) chipColor.copy(alpha = 0.25f) else SurfaceCardElevated)
                                                        .border(1.dp, if (isSelected) chipColor else SurfaceCardBorder, RoundedCornerShape(8.dp))
                                                        .clickable { onGrievancePriorityChange(priority) }
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = priority,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) chipColor else TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Textarea Input Field
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Detailed Grievance Description",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )
                                        OutlinedTextField(
                                            value = uiState.grievanceDescription,
                                            onValueChange = onGrievanceDescChange,
                                            placeholder = {
                                                Text(
                                                    "Describe the issue in detail (e.g., specific teacher concern, exam mark discrepancy, lab equipment)...",
                                                    color = TextMuted,
                                                    fontSize = 13.sp
                                                )
                                            },
                                            minLines = 3,
                                            maxLines = 5,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = IndigoPrimary,
                                                unfocusedBorderColor = SurfaceCardBorder,
                                                focusedContainerColor = SurfaceCardElevated,
                                                unfocusedContainerColor = SurfaceCardElevated,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("grievance_description_input")
                                        )
                                    }

                                    // Submit Grievance Button
                                    Button(
                                        onClick = onSubmitGrievance,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("submit_grievance_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Log & Submit Grievance", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Live Grievance Log List Display
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Submitted Grievances & Live Tickets",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${uiState.grievanceTickets.size} Tickets",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        items(uiState.grievanceTickets) { ticket ->
                            GrievanceTicketCard(ticket = ticket)
                        }
                    }
                }

                // ==================== TAB B: STUDENT PROFILE ====================
                StudentTab.STUDENT_PROFILE -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Dense Personal Data Roster
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceCard
                            ) {
                                Column(
                                    modifier = Modifier
                                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(EmeraldSuccess, EmeraldDark)
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "AM",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Alex Miller",
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Reg No: 2026-STU-8821 • Batch 2024-2028",
                                                fontSize = 11.sp,
                                                color = EmeraldLight
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = SurfaceCardBorder)

                                    // Dense Data Grid (Name, Gender, Mother Tongue, Nationality, DOB, Religion, Community, Blood Group)
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            ProfileRosterField("Full Name", "Alex David Miller", Modifier.weight(1f))
                                            ProfileRosterField("Gender", "Male", Modifier.weight(1f))
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            ProfileRosterField("Mother Tongue", "English", Modifier.weight(1f))
                                            ProfileRosterField("Nationality", "Indian / Citizen", Modifier.weight(1f))
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            ProfileRosterField("Date of Birth", "14-Aug-2005 (21 Yrs)", Modifier.weight(1f))
                                            ProfileRosterField("Blood Group", "O+ Positive", Modifier.weight(1f))
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            ProfileRosterField("Religion", "Non-Denominational", Modifier.weight(1f))
                                            ProfileRosterField("Community", "OBC (Category-II)", Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }

                        // "PHOTOCOPY ADD" SECTION (TURNS GREEN WHEN FILE IS SELECTED)
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(6.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                color = SurfaceCard
                            ) {
                                Column(
                                    modifier = Modifier
                                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(20.dp))
                                        .padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Attachment,
                                                contentDescription = null,
                                                tint = EmeraldLight,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = "Photocopy Add & Verification",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        val uploadedCount = uiState.documents.count { it.isUploaded }
                                        Text(
                                            text = "$uploadedCount/4 Attached",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uploadedCount == 4) EmeraldLight else SkyInfo
                                        )
                                    }

                                    Text(
                                        text = "Tap any certificate box to attach/toggle files. Verified files turn vibrant green.",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    // 4 Photocopy Upload Tiles
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        uiState.documents.forEach { doc ->
                                            DocumentUploadTile(
                                                document = doc,
                                                onToggle = { onToggleDocument(doc.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // EXPANDABLE CARD CLUSTERS (Father, Mother, Guardian)
                        item {
                            Text(
                                text = "Family & Guardian Contact Clusters",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Cluster 1: Father Details
                        item {
                            ExpandableClusterCard(
                                title = "Father's Details",
                                subtitle = "David Miller • Senior Civil Engineer",
                                icon = Icons.Default.Person,
                                isExpanded = isFatherExpanded,
                                onToggle = { isFatherExpanded = !isFatherExpanded }
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ClusterDataRow("Full Name", "David Miller")
                                    ClusterDataRow("Occupation", "Senior Civil Engineer (L&T Infra)")
                                    ClusterDataRow("Mobile Contact", "+1 (555) 432-9012")
                                    ClusterDataRow("Email ID", "david.miller@example.com")
                                    ClusterDataRow("Annual Income", "₹8,50,000 / Year (Verified)")
                                }
                            }
                        }

                        // Cluster 2: Mother Details
                        item {
                            ExpandableClusterCard(
                                title = "Mother's Details",
                                subtitle = "Sarah Miller • Healthcare Administrator",
                                icon = Icons.Default.Person2,
                                isExpanded = isMotherExpanded,
                                onToggle = { isMotherExpanded = !isMotherExpanded }
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ClusterDataRow("Full Name", "Sarah Miller")
                                    ClusterDataRow("Occupation", "Healthcare Administrator (General Hospital)")
                                    ClusterDataRow("Mobile Contact", "+1 (555) 432-9013")
                                    ClusterDataRow("Email ID", "sarah.miller@example.com")
                                    ClusterDataRow("Annual Income", "₹7,20,000 / Year (Verified)")
                                }
                            }
                        }

                        // Cluster 3: Guardian Details
                        item {
                            ExpandableClusterCard(
                                title = "Local Guardian Details",
                                subtitle = "James Miller • Uncle & Local Domicile",
                                icon = Icons.Default.Shield,
                                isExpanded = isGuardianExpanded,
                                onToggle = { isGuardianExpanded = !isGuardianExpanded }
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ClusterDataRow("Guardian Name", "James Miller")
                                    ClusterDataRow("Relationship", "Paternal Uncle / Local Guardian")
                                    ClusterDataRow("Mobile Contact", "+1 (555) 776-2301")
                                    ClusterDataRow("Residential Address", "442 Elmwood Crest, Suite 4B, Metro District")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isScholarshipSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isScholarshipSheetOpen = false },
            containerColor = colors.surfaceCardElevated,
            dragHandle = { BottomSheetDefaults.DragHandle(color = colors.textMuted) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with scheme icon and counter
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
                                .background(EmeraldSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "State Scholarships",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EmeraldSuccess.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${uiState.scholarships.size} SCHEMES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                }
                            }
                            Text(
                                text = "Tamil Nadu DBT Welfare Grants • Apply & Submit Online",
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }

                    IconButton(onClick = { isScholarshipSheetOpen = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = colors.textMuted)
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.scholarships) { item ->
                        ScholarshipCard(
                            scholarship = item,
                            viewMode = ScholarshipViewMode.STUDENT,
                            onAction = onApproveScholarship
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun ProfileRosterField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
fun DocumentUploadTile(
    document: DocumentItem,
    onToggle: () -> Unit
) {
    val colors = AppTheme.colors
    val isUploaded = document.isUploaded

    // Clear theme-aware color mapping for maximum visibility in both Light & Dark modes
    val tileBackground = if (isUploaded) {
        if (colors.isDark) EmeraldBg.copy(alpha = 0.5f) else Color(0xFFDCFCE7)
    } else {
        colors.surfaceCardElevated
    }

    val tileBorderColor = if (isUploaded) {
        if (colors.isDark) EmeraldSuccess else EmeraldDark
    } else {
        colors.surfaceCardBorder
    }

    val iconBackground = if (isUploaded) {
        EmeraldSuccess
    } else {
        if (colors.isDark) SurfaceCardElevated else Color(0xFFE2E8F0)
    }

    val iconTint = if (isUploaded) {
        Color.White
    } else {
        colors.textMuted
    }

    val titleColor = if (isUploaded) {
        if (colors.isDark) EmeraldLight else Color(0xFF14532D)
    } else {
        colors.textPrimary
    }

    val subtitleColor = if (isUploaded) {
        if (colors.isDark) Color(0xFFA7F3D0) else Color(0xFF166534)
    } else {
        colors.textSecondary
    }

    val buttonBackground = if (isUploaded) {
        if (colors.isDark) EmeraldSuccess else Color(0xFF16A34A)
    } else {
        IndigoPrimary.copy(alpha = if (colors.isDark) 0.2f else 0.12f)
    }

    val buttonTextColor = if (isUploaded) {
        Color.White
    } else {
        IndigoPrimary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .testTag("doc_tile_${document.id}"),
        shape = RoundedCornerShape(12.dp),
        color = tileBackground
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, tileBorderColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = document.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    Text(
                        text = if (isUploaded) {
                            "${document.fileName} • ${document.fileSize}"
                        } else {
                            document.subtitle
                        },
                        fontSize = 11.sp,
                        color = subtitleColor,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(buttonBackground)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isUploaded) "ATTACHED ✓" else "CHOOSE FILE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttonTextColor
                )
            }
        }
    }
}

@Composable
fun ExpandableClusterCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = IndigoLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
                    }
                }

                IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle",
                        tint = TextMuted
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(SurfaceCardElevated, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ClusterDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
fun GrievanceTicketCard(ticket: GrievanceTicket) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "#${ticket.id}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Text(
                        text = "• ${ticket.issueType}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                ticket.status.contains("Resolved") -> EmeraldSuccess.copy(alpha = 0.2f)
                                ticket.status.contains("Review") -> AmberWarning.copy(alpha = 0.2f)
                                else -> SkyInfo.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = ticket.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            ticket.status.contains("Resolved") -> EmeraldLight
                            ticket.status.contains("Review") -> AmberWarning
                            else -> SkyInfo
                        }
                    )
                }
            }

            Text(
                text = ticket.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Priority: ${ticket.priority}",
                    fontSize = 11.sp,
                    color = when (ticket.priority) {
                        "Urgent" -> RoseError
                        "High" -> AmberWarning
                        else -> TextMuted
                    },
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = ticket.timestamp,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
