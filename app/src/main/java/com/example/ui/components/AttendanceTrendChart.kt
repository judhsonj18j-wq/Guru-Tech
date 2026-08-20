package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class AttendanceDataPoint(
    val label: String,
    val percentage: Float, // 0.0 to 100.0
    val presentCount: Int,
    val totalCount: Int,
    val dateString: String
)

@Composable
fun AttendanceTrendChartCard(
    title: String,
    subtitle: String,
    dataPoints: List<AttendanceDataPoint>,
    thresholdPercentage: Float = 75f,
    primaryColor: Color = EmeraldSuccess,
    secondaryColor: Color = IndigoLight,
    isTeacherView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var selectedIndex by remember { mutableStateOf(dataPoints.size - 1) }
    val animationProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(dataPoints) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }

    val selectedPoint = dataPoints.getOrNull(selectedIndex) ?: dataPoints.lastOrNull()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = colors.surfaceCard
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, colors.surfaceCardBorder, RoundedCornerShape(20.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Chart Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(primaryColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                // Interactive Selected Tooltip Badge
                if (selectedPoint != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceCardElevated)
                            .border(1.dp, primaryColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor)
                            )
                            Text(
                                text = "${selectedPoint.percentage}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "(${selectedPoint.label})",
                                fontSize = 10.sp,
                                color = colors.textMuted
                            )
                        }
                    }
                }
            }

            // Key Summary Metric Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        val currentVal = selectedPoint?.percentage ?: 91.5f
                        Text(
                            text = String.format("%.1f", currentVal),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = if (currentVal >= thresholdPercentage) primaryColor else AmberWarning
                        )
                        Text(
                            text = "%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                        )
                    }
                    Text(
                        text = if (selectedPoint != null) {
                            "${selectedPoint.dateString} • ${selectedPoint.presentCount}/${selectedPoint.totalCount} ${if (isTeacherView) "Students" else "Days"}"
                        } else {
                            "Average Attendance"
                        },
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                // Legend Pills
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                        Text(
                            text = if (isTeacherView) "Class Rate" else "Student",
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.dp)
                                .background(AmberWarning)
                        )
                        Text(
                            text = "Min 75%",
                            fontSize = 10.sp,
                            color = AmberWarning
                        )
                    }
                }
            }

            // High-Performance Custom Canvas Recharts-style Area & Trend Graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceCardElevated)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(dataPoints) {
                            detectTapGestures { offset ->
                                val spacing = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                                val index = (offset.x / spacing).toInt().coerceIn(0, dataPoints.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 24.dp.toPx()
                    val topPadding = 12.dp.toPx()
                    val chartHeight = height - bottomPadding - topPadding

                    // Draw Horizontal Grid lines (100%, 75% threshold, 50%)
                    val y100 = topPadding
                    val y75 = topPadding + chartHeight * (1f - (thresholdPercentage / 100f))
                    val y50 = topPadding + chartHeight * 0.5f

                    // 100% Top Grid line
                    drawLine(
                        color = colors.surfaceCardBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y100),
                        end = Offset(width, y100),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "100%",
                        topLeft = Offset(4.dp.toPx(), y100 - 10.dp.toPx()),
                        style = TextStyle(fontSize = 9.sp, color = colors.textMuted)
                    )

                    // 75% Threshold Guideline (Dashed in Amber/Rose)
                    drawLine(
                        color = AmberWarning.copy(alpha = 0.75f),
                        start = Offset(0f, y75),
                        end = Offset(width, y75),
                        strokeWidth = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "75% Min",
                        topLeft = Offset(4.dp.toPx(), y75 - 10.dp.toPx()),
                        style = TextStyle(fontSize = 9.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                    )

                    // 50% Grid line
                    drawLine(
                        color = colors.surfaceCardBorder.copy(alpha = 0.3f),
                        start = Offset(0f, y50),
                        end = Offset(width, y50),
                        strokeWidth = 1.dp.toPx()
                    )

                    if (dataPoints.isEmpty()) return@Canvas

                    val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)
                    val points = dataPoints.mapIndexed { index, point ->
                        val x = index * stepX
                        val normalizedY = ((point.percentage.coerceIn(0f, 100f)) / 100f) * animationProgress.value
                        val y = topPadding + chartHeight * (1f - normalizedY)
                        Offset(x, y)
                    }

                    // Build Smooth Gradient Area Path
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height - bottomPadding)
                        lineTo(points.first().x, points.first().y)
                        
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                        
                        lineTo(points.last().x, height - bottomPadding)
                        close()
                    }

                    // Draw Gradient Fill Area
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.45f),
                                primaryColor.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = topPadding,
                            endY = height - bottomPadding
                        )
                    )

                    // Build & Draw Trend Stroke Line
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }
                    }

                    drawPath(
                        path = strokePath,
                        brush = Brush.horizontalGradient(
                            listOf(primaryColor, secondaryColor)
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw Data Points & X-Axis Labels
                    points.forEachIndexed { index, pointOffset ->
                        val isSelected = index == selectedIndex
                        val dataPoint = dataPoints[index]

                        // X-axis label below point
                        val labelLayout = textMeasurer.measure(
                            text = dataPoint.label,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = if (isSelected) primaryColor else colors.textMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        drawText(
                            textLayoutResult = labelLayout,
                            topLeft = Offset(
                                pointOffset.x - (labelLayout.size.width / 2f),
                                height - bottomPadding + 6.dp.toPx()
                            )
                        )

                        // Outer Glow Ring for Selected Point
                        if (isSelected) {
                            // Vertical guide line
                            drawLine(
                                color = primaryColor.copy(alpha = 0.35f),
                                start = Offset(pointOffset.x, topPadding),
                                end = Offset(pointOffset.x, height - bottomPadding),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )

                            drawCircle(
                                color = primaryColor.copy(alpha = 0.3f),
                                radius = 10.dp.toPx(),
                                center = pointOffset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = pointOffset
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = 3.dp.toPx(),
                                center = pointOffset
                            )
                        } else {
                            drawCircle(
                                color = colors.surfaceCard,
                                radius = 4.5.dp.toPx(),
                                center = pointOffset
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = 3.dp.toPx(),
                                center = pointOffset
                            )
                        }
                    }
                }
            }

            // Footer Insight Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IndigoLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Tap any node to view daily records & roll totals",
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }

                Text(
                    text = "Weekly Peak: 96.5%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }
        }
    }
}
