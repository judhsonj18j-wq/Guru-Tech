package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.EduSmartUiState
import com.example.ui.components.ScholarshipCard
import com.example.ui.components.ScholarshipViewMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    uiState: EduSmartUiState,
    onParentAction: (String) -> Unit,
    onApproveScholarship: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mentorPhoneNumber = "+916382835276"
    val formattedMentorPhone = "+91 6382835276"

    var isScholarshipSheetOpen by remember { mutableStateOf(false) }
    var isApplyLeaveSheetOpen by remember { mutableStateOf(false) }
    var leaveReason by remember { mutableStateOf("") }
    var leaveCategory by remember { mutableStateOf("Medical / Sick Leave") }
    var leaveDuration by remember { mutableStateOf("1 Day") }
    var leaveReasonError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Child Overview Profile Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
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
                        .border(1.dp, SkyInfo.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(SkyInfo, IndigoPrimary)
                                        )
                                    )
                                    .border(2.dp, SkyInfo, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AM",
                                    fontSize = 18.sp,
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
                                        text = "Alex Miller",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldSuccess.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Active Student",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                    }
                                }
                                Text(
                                    text = "Grade 10-A • Roll No: 28 • Reg: 2026-STU881",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = SkyInfo,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Mentor: Prof. Robert Miller ($formattedMentorPhone)",
                                        fontSize = 11.sp,
                                        color = SkyInfo
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = SurfaceCardBorder)

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    leaveReason = ""
                                    leaveReasonError = false
                                    isApplyLeaveSheetOpen = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("parent_btn_leave"),
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(SurfaceCardBorder, AmberWarning.copy(alpha = 0.4f)))
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.EventBusy, contentDescription = null, modifier = Modifier.size(16.dp), tint = AmberWarning)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Leave", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = {
                                    try {
                                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:$mentorPhoneNumber")
                                        }
                                        context.startActivity(dialIntent)
                                    } catch (e: Exception) {
                                        // Fallback if dialer intent not available
                                    }
                                    onParentAction("call_mentor")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("parent_btn_contact"),
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(listOf(SurfaceCardBorder, SkyInfo.copy(alpha = 0.4f)))
                                ),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = SkyInfo)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call Mentor", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Graphical Attendance Trend Visualization (Replacing text-based metrics)
        item {
            com.example.ui.components.AttendanceTrendChartCard(
                title = "Student Attendance Curve",
                subtitle = "5-Week Trend • Alex Miller (146/160 Total Days)",
                dataPoints = uiState.parentStudentAttendanceTrend,
                thresholdPercentage = 75f,
                primaryColor = EmeraldSuccess,
                secondaryColor = SkyInfo,
                isTeacherView = false,
                modifier = Modifier.testTag("parent_attendance_trend_chart")
            )
        }

        // Academic CGPA Metrics Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(18.dp))
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = IndigoLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Academic CGPA & Scores",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IndigoPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Rank #3 in Class",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoLight
                            )
                        }
                    }

                    // Key CGPA Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricBox(
                            title = "Cumulative CGPA",
                            value = "8.92",
                            subtext = "Scale of 10.0",
                            accentColor = IndigoLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Term 1 Grade",
                            value = "9.10",
                            subtext = "Grade A+",
                            accentColor = EmeraldLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Term 2 Grade",
                            value = "8.74",
                            subtext = "Grade A",
                            accentColor = SkyInfo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = SurfaceCardBorder)

                    // Subject Breakdown
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SubjectScoreRow(subject = "Computer Science & Data Structures", score = "94 / 100", grade = "O (Outstanding)")
                        SubjectScoreRow(subject = "Applied Mathematics & Calculus", score = "88 / 100", grade = "A+ (Excellent)")
                        SubjectScoreRow(subject = "Digital Electronics & Circuits", score = "86 / 100", grade = "A+ (Excellent)")
                    }
                }
            }
        }

        // Active Government Scholarships & Parent Approval Portal (Sleek Banner + DBT Timeline + Modal Trigger)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with Quick Action
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
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Scholarships & Welfare Grants",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${uiState.scholarships.size} SCHEMES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }

                    Text(
                        text = "Official Tamil Nadu higher education welfare schemes. Review schemes and provide parent consent directly online.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    // Sleek Action Button to open Scholarships Modal
                    Button(
                        onClick = { isScholarshipSheetOpen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("parent_scholarships_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Review & Endorse Scholarships (${uiState.scholarships.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    HorizontalDivider(color = SurfaceCardBorder)

                    // Live DBT Tracker for active student Alex Miller
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Disbursement Tracker (Tamil Pudhalvan)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Direct DBT",
                            fontSize = 11.sp,
                            color = SkyInfo
                        )
                    }

                    // Timeline Steps
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimelineStepRow(
                            stepNumber = 1,
                            title = "Application & Parent Consent",
                            subtitle = "Submitted online with income certificate",
                            date = "Aug 02, 2026",
                            isCompleted = true,
                            isCurrent = false
                        )
                        TimelineStepRow(
                            stepNumber = 2,
                            title = "Document & School Verification",
                            subtitle = "Govt 6th-12th school study verified by Cell",
                            date = "Aug 10, 2026",
                            isCompleted = true,
                            isCurrent = false
                        )
                        TimelineStepRow(
                            stepNumber = 3,
                            title = "Faculty & Nodal Officer Approval",
                            subtitle = "Endorsed by Prof. Robert Miller",
                            date = "Aug 16, 2026",
                            isCompleted = true,
                            isCurrent = false
                        )
                        TimelineStepRow(
                            stepNumber = 4,
                            title = "State Treasury DBT Disbursement",
                            subtitle = "Direct benefit transfer into student Aadhaar-linked account",
                            date = "Estimated: Aug 28",
                            isCompleted = false,
                            isCurrent = true
                        )
                    }
                }
            }
        }

        // Report Card Download Action
        item {
            Button(
                onClick = { onParentAction("report") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("download_report_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Official Semester Report Card (PDF)", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Parent Scholarship Review Bottom Sheet
    if (isScholarshipSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isScholarshipSheetOpen = false },
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
                                .background(EmeraldSuccess.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
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
                                    text = "Scholarship Consents",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
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
                                text = "Endorse State Welfare Grants for Alex Miller",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = { isScholarshipSheetOpen = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 440.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.scholarships) { item ->
                        ScholarshipCard(
                            scholarship = item,
                            viewMode = ScholarshipViewMode.PARENT,
                            onAction = onApproveScholarship
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    // Parent Apply Leave Bottom Sheet Modal
    if (isApplyLeaveSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isApplyLeaveSheetOpen = false },
            containerColor = SurfaceCardElevated,
            dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberWarning.copy(alpha = 0.2f))
                                .border(1.dp, AmberWarning.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = "Apply Student Leave",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Alex Miller (Roll #28) • Sent to Mentor Prof. Robert Miller",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = { isApplyLeaveSheetOpen = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = SurfaceCardBorder)

                // Leave Category Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Leave Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    val categories = listOf("Medical / Sick", "Family Function", "Personal", "Emergency")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = leaveCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) AmberWarning.copy(alpha = 0.22f) else SurfaceCard
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AmberWarning else SurfaceCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { leaveCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AmberWarning else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Leave Duration Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Duration",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    val durations = listOf("Half Day", "1 Day", "2 Days", "3+ Days")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        durations.forEach { dur ->
                            val isSelected = leaveDuration == dur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) SkyInfo.copy(alpha = 0.22f) else SurfaceCard
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyInfo else SurfaceCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { leaveDuration = dur }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dur,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SkyInfo else TextSecondary
                                )
                            }
                        }
                    }
                }

                // Reason for Leave Text Box
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "State Reason for Leave *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        if (leaveReason.isNotBlank()) {
                            Text(
                                text = "${leaveReason.length} chars",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    OutlinedTextField(
                        value = leaveReason,
                        onValueChange = {
                            leaveReason = it
                            if (it.isNotBlank()) leaveReasonError = false
                        },
                        placeholder = {
                            Text(
                                text = "Describe the reason in detail (e.g., Alex is diagnosed with fever & advised 2 days bed rest by doctor)...",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            .testTag("parent_leave_reason_input"),
                        minLines = 3,
                        maxLines = 5,
                        isError = leaveReasonError,
                        supportingText = {
                            if (leaveReasonError) {
                                Text(
                                    text = "Please enter the reason for leave before sending",
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberWarning,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Quick Reason Suggestions
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Quick Suggestions:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    val quickTemplates = listOf(
                        "Suffering from viral fever & doctor advised bed rest.",
                        "Attending family wedding function in hometown.",
                        "Medical checkup and specialist consultation.",
                        "Urgent personal family obligation."
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickTemplates) { template ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceCard)
                                    .border(0.8.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        leaveReason = template
                                        leaveReasonError = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = template,
                                    fontSize = 10.5.sp,
                                    color = IndigoLight,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Mentor Info Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SkyInfo.copy(alpha = 0.12f))
                        .border(0.8.dp, SkyInfo.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SkyInfo,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Mentor Phone: $formattedMentorPhone • Instant SMS/Portal notification sent upon submit.",
                        fontSize = 10.5.sp,
                        color = SkyInfo,
                        lineHeight = 14.sp
                    )
                }

                // Action Buttons (Cancel & Send)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { isApplyLeaveSheetOpen = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(SurfaceCardBorder, SurfaceCardBorder))
                        )
                    ) {
                        Text("Cancel", color = TextSecondary, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (leaveReason.trim().isEmpty()) {
                                leaveReasonError = true
                            } else {
                                onParentAction("leave:$leaveCategory ($leaveDuration) - ${leaveReason.trim()}")
                                leaveReason = ""
                                isApplyLeaveSheetOpen = false
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                            .testTag("parent_send_leave_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF0A0E1A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Send Application",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0A0E1A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, fontSize = 10.sp, color = TextMuted, maxLines = 1)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Text(text = subtext, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun SubjectScoreRow(subject: String, score: String, grade: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = subject,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = score, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = grade, fontSize = 11.sp, color = EmeraldLight)
        }
    }
}

@Composable
fun TimelineStepRow(
    stepNumber: Int,
    title: String,
    subtitle: String,
    date: String,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Step indicator icon
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> EmeraldSuccess
                        isCurrent -> AmberWarning
                        else -> SurfaceCardElevated
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isCurrent) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = Color(0xFF0A0E1A),
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text(
                    text = "$stepNumber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted || isCurrent) TextPrimary else TextMuted
                )
                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = if (isCurrent) AmberWarning else TextMuted,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
            }
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
