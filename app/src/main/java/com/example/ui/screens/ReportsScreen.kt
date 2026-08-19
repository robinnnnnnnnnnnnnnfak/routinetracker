package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.DailySummary
import com.example.ui.model.MonthlyReport
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurplePrimary
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ReportsScreen(
    report: MonthlyReport,
    selectedYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthName = selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val year = selectedYearMonth.year

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp)
    ) {
        // Month Selector Header Item
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onYearMonthSelected(selectedYearMonth.minusMonths(1)) },
                        modifier = Modifier.testTag("report_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = TextSecondaryDark
                        )
                    }

                    Text(
                        text = "Report: $monthName $year",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )

                    IconButton(
                        onClick = { onYearMonthSelected(selectedYearMonth.plusMonths(1)) },
                        modifier = Modifier.testTag("report_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Overall Monthly Overview Card Item
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Monthly Overview",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReportMetric(
                            label = "Overall Completion",
                            value = "${String.format(Locale.US, "%.1f", report.overallCompletionPercentage)}%",
                            color = AccentPurple
                        )
                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = OutlineDark
                        )
                        ReportMetric(
                            label = "Total Scheduled",
                            value = "${report.totalScheduledOccurrences}",
                            color = AccentPurple
                        )
                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = OutlineDark
                        )
                        ReportMetric(
                            label = "Done / Missed",
                            value = "${report.totalCompletedOccurrences} / ${report.totalMissedOccurrences}",
                            color = AccentPurple
                        )
                    }
                }
            }
        }

        // Real Aesthetic Daily Performance Line Graph Item
        item {
            DailyPerformanceLineGraphCard(
                dailyPoints = report.dailyPoints,
                monthName = monthName
            )
        }

        // Best Day & Worst Day Cards Item
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Best Day Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("best_day_card")
                        .border(1.dp, OutlineDark, RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Best Day",
                                tint = StatusGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BEST DAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (report.bestDay != null) {
                            val dateFmt = report.bestDay.date.format(
                                DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
                            )
                            Text(
                                text = dateFmt,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${report.bestDay.completedCount}/${report.bestDay.totalScheduled} completed",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.0f", report.bestDay.completionPercentage)}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            )
                        } else {
                            Text(
                                text = "No data for this month.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextTertiaryDark
                                )
                            )
                        }
                    }
                }

                // Worst Day Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("worst_day_card")
                        .border(1.dp, OutlineDark, RoundedCornerShape(16.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Worst Day",
                                tint = StatusRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WORST DAY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StatusRed
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (report.worstDay != null) {
                            val dateFmt = report.worstDay.date.format(
                                DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
                            )
                            Text(
                                text = dateFmt,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${report.worstDay.completedCount}/${report.worstDay.totalScheduled} done (${report.worstDay.missedCount} missed)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.0f", report.worstDay.completionPercentage)}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StatusRed
                                )
                            )

                            if (report.worstDay.missedTaskTitles.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Missed: ${report.worstDay.missedTaskTitles.joinToString(", ")}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusRed,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 2
                                )
                            }
                        } else {
                            Text(
                                text = "No data for this month.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextTertiaryDark
                                )
                            )
                        }
                    }
                }
            }
        }

        // Process / Trend Analysis Card Item
        item {
            val trend = report.trendAnalysis
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val trendIcon = when {
                            trend.percentagePointChange > 2.0 -> Icons.Default.TrendingUp
                            trend.percentagePointChange < -2.0 -> Icons.Default.TrendingDown
                            else -> Icons.Default.Star
                        }
                        val trendColor = when {
                            trend.percentagePointChange > 2.0 -> StatusGreen
                            trend.percentagePointChange < -2.0 -> StatusRed
                            else -> AccentPurple
                        }

                        Icon(
                            imageVector = trendIcon,
                            contentDescription = "Trend Icon",
                            tint = trendColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Process / Trend Analysis",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (trend.hasEnoughData) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TrendMetric(
                                label = "Earlier average",
                                value = "${String.format(Locale.US, "%.1f", trend.earlierPeriodAvg)}%"
                            )
                            TrendMetric(
                                label = "Recent average",
                                value = "${String.format(Locale.US, "%.1f", trend.laterPeriodAvg)}%"
                            )
                            TrendMetric(
                                label = "Change",
                                value = "${if (trend.percentagePointChange >= 0) "+" else ""}${String.format(Locale.US, "%.1f", trend.percentagePointChange)} points",
                                isHighlight = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = OutlineDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = trend.statement,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple
                            )
                        )
                    } else {
                        Text(
                            text = trend.statement,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondaryDark
                            )
                        )
                    }
                }
            }
        }

        // Task Consistency Breakdown Section Header
        item {
            Text(
                text = "Task Consistency Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Task Consistency Cards
        items(report.taskStats, key = { "report_stat_${it.task.id}" }) { stat ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(14.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stat.task.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TrendMetric(label = "Completed", value = "${stat.completedDays} days")
                        TrendMetric(label = "Scheduled", value = "${stat.scheduledDays} days")
                        TrendMetric(label = "Missed", value = "${stat.missedDays} days")
                        TrendMetric(
                            label = "Consistency",
                            value = "${String.format(Locale.US, "%.1f", stat.consistencyPercentage)}%",
                            isHighlight = true
                        )
                    }
                }
            }
        }

        // Month-to-Month Comparison Card Item
        item {
            val comp = report.monthComparison
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Month-to-Month Comparison",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (comp.hasPreviousData) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonthComparisonItem(
                                monthName = comp.previousMonthName,
                                pct = comp.previousMonthPct
                            )
                            MonthComparisonItem(
                                monthName = comp.currentMonthName,
                                pct = comp.currentMonthPct
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val changeSign = if (comp.percentagePointChange >= 0) "+" else ""
                        val changeText = "$changeSign${String.format(Locale.US, "%.1f", comp.percentagePointChange)} percentage points"
                        val resultText = when {
                            comp.percentagePointChange > 0 -> "Process improved compared to ${comp.previousMonthName}."
                            comp.percentagePointChange < 0 -> "Process decreased compared to ${comp.previousMonthName}."
                            else -> "Process remained unchanged compared to ${comp.previousMonthName}."
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, OutlineDark, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Change: $changeText",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentPurple
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = resultText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark
                                    )
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Previous month data (${comp.previousMonthName}) is not available yet for comparison.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondaryDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyPerformanceLineGraphCard(
    dailyPoints: List<DailySummary>,
    monthName: String
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_performance_line_graph_card")
            .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = AccentPurple.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = "Daily Performance Graph",
                                tint = AccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Performance Trend",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondaryDark
                            )
                        )
                    }
                }

                if (dailyPoints.isNotEmpty()) {
                    val avgPct = dailyPoints.map { it.completionPercentage }.average()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentPurple.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentPurple.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Avg ${String.format(Locale.US, "%.0f", avgPct)}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AccentPurple
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dailyPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No daily records available for $monthName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                }
            } else {
                val selectedSummary = selectedIndex?.let { dailyPoints.getOrNull(it) }

                AnimatedVisibility(
                    visible = selectedSummary != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    selectedSummary?.let { summary ->
                        val dateStr = summary.date.format(
                            DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, OutlineDark, RoundedCornerShape(12.dp))
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = "${summary.completedCount}/${summary.totalScheduled} Tasks (${String.format(Locale.US, "%.0f", summary.completionPercentage)}%)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentPurple
                                    )
                                )
                            }
                        }
                    }
                }

                val primaryColor = AccentPurplePrimary
                val gridColor = OutlineDark.copy(alpha = 0.5f)
                val textColor = TextSecondaryDark
                val surfaceColor = MaterialTheme.colorScheme.surface

                val textMeasurer = rememberTextMeasurer()
                val textStyle = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontSize = 10.sp
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(dailyPoints) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val leftPadding = 80f
                                val rightPadding = 20f
                                val graphWidth = width - leftPadding - rightPadding

                                if (dailyPoints.size > 1 && graphWidth > 0) {
                                    val touchX = tapOffset.x - leftPadding
                                    val fraction = (touchX / graphWidth).coerceIn(0f, 1f)
                                    val index = (fraction * (dailyPoints.size - 1)).roundToInt()
                                    selectedIndex = index.coerceIn(0, dailyPoints.size - 1)
                                } else if (dailyPoints.size == 1) {
                                    selectedIndex = 0
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val leftPadding = 80f
                    val rightPadding = 20f
                    val topPadding = 20f
                    val bottomPadding = 45f

                    val graphWidth = width - leftPadding - rightPadding
                    val graphHeight = height - topPadding - bottomPadding

                    // 1. Draw Horizontal Y-Axis Grid Lines & Percentage Labels
                    val yLevels = listOf(1.0f, 0.75f, 0.5f, 0.25f, 0.0f)
                    yLevels.forEach { pct ->
                        val y = topPadding + graphHeight * (1f - pct)
                        val label = "${(pct * 100).toInt()}%"

                        drawLine(
                            color = gridColor,
                            start = Offset(leftPadding, y),
                            end = Offset(width - rightPadding, y),
                            strokeWidth = 1.2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )

                        val textResult = textMeasurer.measure(label, textStyle)
                        drawText(
                            textLayoutResult = textResult,
                            topLeft = Offset(leftPadding - textResult.size.width - 12f, y - textResult.size.height / 2f)
                        )
                    }

                    // 2. Compute Points
                    val points = dailyPoints.mapIndexed { index, summary ->
                        val x = if (dailyPoints.size > 1) {
                            leftPadding + (index.toFloat() / (dailyPoints.size - 1)) * graphWidth
                        } else {
                            leftPadding + graphWidth / 2f
                        }
                        val pct = (summary.completionPercentage / 100.0).toFloat().coerceIn(0f, 1f)
                        val y = topPadding + graphHeight * (1f - pct)
                        Offset(x, y)
                    }

                    // 3. Draw X-Axis Date Labels (Intelligent step)
                    val labelStep = when {
                        dailyPoints.size > 25 -> 5
                        dailyPoints.size > 15 -> 3
                        dailyPoints.size > 8 -> 2
                        else -> 1
                    }

                    dailyPoints.forEachIndexed { index, summary ->
                        if (index == 0 || index == dailyPoints.size - 1 || index % labelStep == 0) {
                            val x = points[index].x
                            val dateLabel = "${summary.date.dayOfMonth}"
                            val textResult = textMeasurer.measure(dateLabel, textStyle)
                            drawText(
                                textLayoutResult = textResult,
                                topLeft = Offset(x - textResult.size.width / 2f, height - bottomPadding + 8f)
                            )
                        }
                    }

                    // 4. Draw Smooth Cubic Bezier Line & Gradient Area Fill
                    if (points.size >= 2) {
                        val strokePath = Path()
                        val fillPath = Path()

                        strokePath.moveTo(points[0].x, points[0].y)
                        fillPath.moveTo(points[0].x, height - bottomPadding)
                        fillPath.lineTo(points[0].x, points[0].y)

                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val dx = p2.x - p1.x
                            val control1 = Offset(p1.x + dx / 2f, p1.y)
                            val control2 = Offset(p2.x - dx / 2f, p2.y)

                            strokePath.cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                            fillPath.cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                        }

                        fillPath.lineTo(points.last().x, height - bottomPadding)
                        fillPath.close()

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.35f),
                                    primaryColor.copy(alpha = 0.02f)
                                ),
                                startY = topPadding,
                                endY = height - bottomPadding
                            )
                        )

                        drawPath(
                            path = strokePath,
                            color = primaryColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    } else if (points.size == 1) {
                        drawCircle(color = primaryColor, radius = 6.dp.toPx(), center = points[0])
                    }

                    // 5. Draw Data Points & Highlight Selected Index
                    points.forEachIndexed { index, point ->
                        val isSelected = index == selectedIndex
                        val radius = if (isSelected) 6.5.dp.toPx() else 4.dp.toPx()

                        if (isSelected) {
                            drawCircle(
                                color = primaryColor.copy(alpha = 0.25f),
                                radius = 14.dp.toPx(),
                                center = point
                            )
                            drawLine(
                                color = primaryColor.copy(alpha = 0.6f),
                                start = Offset(point.x, topPadding),
                                end = Offset(point.x, height - bottomPadding),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }

                        drawCircle(
                            color = primaryColor,
                            radius = radius,
                            center = point
                        )
                        drawCircle(
                            color = surfaceColor,
                            radius = radius / 2f,
                            center = point
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tap on any point to view exact daily completion",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportMetric(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondaryDark,
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
private fun TrendMetric(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondaryDark
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) AccentPurple else TextPrimaryDark
            )
        )
    }
}

@Composable
private fun MonthComparisonItem(
    monthName: String,
    pct: Double
) {
    Column {
        Text(
            text = monthName,
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextSecondaryDark
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${String.format(Locale.US, "%.1f", pct)}%",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        )
    }
}

