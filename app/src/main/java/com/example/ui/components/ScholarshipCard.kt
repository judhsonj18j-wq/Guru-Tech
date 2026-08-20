package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScholarshipItem
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SkyInfo

enum class ScholarshipViewMode {
    STUDENT,
    PARENT,
    TEACHER
}

/**
 * Sleek, compact, and polished Scholarship Card.
 * Displays the complete, unabbreviated official scholarship name with optimal
 * typography hierarchy, crisp category styling, and neat layout structure.
 */
@Composable
fun ScholarshipCard(
    scholarship: ScholarshipItem,
    viewMode: ScholarshipViewMode,
    onAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var isExpanded by remember { mutableStateOf(false) }

    val tagColor = when (scholarship.tag) {
        "Girls" -> Color(0xFFF43F5E)
        "Boys" -> SkyInfo
        "SC/ST/SCC" -> AmberWarning
        "BC/MBC/DNC" -> EmeraldSuccess
        else -> IndigoPrimary
    }

    val tagBg = if (colors.isDark) tagColor.copy(alpha = 0.16f) else tagColor.copy(alpha = 0.10f)

    val isApproved = scholarship.status.contains("Approved") ||
            scholarship.status.contains("Parent Consent") ||
            scholarship.status.contains("Submitted")

    val statusBadgeColor = if (isApproved) EmeraldLight else AmberWarning
    val statusBg = if (isApproved) {
        if (colors.isDark) EmeraldSuccess.copy(alpha = 0.15f) else Color(0xFFDCFCE7)
    } else {
        if (colors.isDark) AmberWarning.copy(alpha = 0.15f) else Color(0xFFFEF3C7)
    }

    val iconVector = when (scholarship.tag) {
        "Girls" -> Icons.Default.School
        "Boys" -> Icons.Default.Person
        "SC/ST/SCC" -> Icons.Default.VerifiedUser
        "BC/MBC/DNC" -> Icons.Default.AccountBalance
        else -> Icons.Default.AccountBalance
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (colors.isDark) 2.dp else 1.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = tagColor.copy(alpha = 0.04f),
                spotColor = tagColor.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (isExpanded) tagColor.copy(alpha = 0.40f) else colors.surfaceCardBorder,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { isExpanded = !isExpanded }
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Category Icon + Tag Pill + Grant Amount Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(tagColor.copy(alpha = 0.22f), tagColor.copy(alpha = 0.08f))
                                )
                            )
                            .border(0.8.dp, tagColor.copy(alpha = 0.35f), RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = tagColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    if (scholarship.tag.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(tagBg)
                                .border(0.6.dp, tagColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = scholarship.tag,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tagColor
                            )
                        }
                    }
                }

                // Grant Amount Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (colors.isDark) EmeraldSuccess.copy(alpha = 0.15f) else Color(0xFFE6F4EA)
                        )
                        .border(
                            0.8.dp,
                            EmeraldLight.copy(alpha = 0.35f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = scholarship.amount,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDark) EmeraldLight else EmeraldDark
                    )
                }
            }

            // Full Unabbreviated Title
            Text(
                text = scholarship.title,
                fontSize = 13.5.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            // Subtitle / Applicant / Description
            Text(
                text = if (viewMode == ScholarshipViewMode.TEACHER) {
                    "Applicant: ${scholarship.applicant} • Applied: ${scholarship.date}"
                } else {
                    scholarship.description
                },
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = colors.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            // Expandable details (Revealed on click)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceCardElevated)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Eligibility & Key Scheme Highlights",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    val eligibilityPoints = when (scholarship.tag) {
                        "Girls" -> listOf(
                            "Studied 6th to 12th standard in Tamil Nadu Government Schools",
                            "Direct DBT transfer of ₹1,000/month directly to student's bank account",
                            "Valid for all Degree, Diploma, ITI and Professional technical courses"
                        )
                        "Boys" -> listOf(
                            "Studied 6th to 12th standard in Tamil Nadu Government Schools",
                            "Monthly financial grant of ₹1,000 via DBT across entire course tenure",
                            "Aadhaar-seeded active bank savings account required"
                        )
                        "SC/ST/SCC" -> listOf(
                            "Parental annual income ceiling: ₹2.50 Lakhs per annum",
                            "100% Tuition fee waiver & special university examination fee waiver",
                            "Hostel grant and special academic allowance included"
                        )
                        "BC/MBC/DNC" -> listOf(
                            "Parental annual income ceiling: ₹2.00 Lakhs per annum",
                            "Post-Matric course tuition fee subsidy & welfare hostel assistance",
                            "Applicable for recognized 3-year & 4-year higher education programs"
                        )
                        else -> listOf(
                            "Tamil Nadu Higher Education Welfare Department scheme",
                            "Faculty endorsement and institutional nodal verification required"
                        )
                    }

                    eligibilityPoints.forEach { point ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(3.5.dp)
                                    .clip(CircleShape)
                                    .background(tagColor)
                            )
                            Text(
                                text = point,
                                fontSize = 10.sp,
                                color = colors.textSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }

            // Bottom Row: Status Tag + Expand Hint + Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Chip + Toggle indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = scholarship.status,
                            fontSize = 10.sp,
                            color = statusBadgeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Less" else "Details",
                            fontSize = 9.5.sp,
                            color = colors.textMuted
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Details",
                            tint = colors.textMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Action Button based on ViewMode
                when (viewMode) {
                    ScholarshipViewMode.STUDENT -> {
                        val isSubmitted = scholarship.status.contains("Approved") || scholarship.status.contains("Submitted")
                        Button(
                            onClick = { onAction(scholarship.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSubmitted) EmeraldDark else IndigoPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isSubmitted) Icons.Default.Check else Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isSubmitted) "Verified ✓" else "Verify & Submit",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    ScholarshipViewMode.PARENT -> {
                        val hasConsent = scholarship.status.contains("Parent Consent") || scholarship.status.contains("Approved")
                        Button(
                            onClick = { onAction(scholarship.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasConsent) EmeraldDark else IndigoPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = if (hasConsent) Icons.Default.CheckCircle else Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (hasConsent) "Consent Granted ✓" else "Approve & Consent",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    ScholarshipViewMode.TEACHER -> {
                        val isFacultyApproved = scholarship.status.contains("Approved by Faculty")
                        if (isFacultyApproved) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldSuccess.copy(alpha = 0.18f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Endorsed ✓",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { onAction(scholarship.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Faculty Approve",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
