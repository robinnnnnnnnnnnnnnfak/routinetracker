package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.TaskGridRow
import com.example.ui.model.TaskStatus
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlyScreen(
    selectedYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit,
    gridRows: List<TaskGridRow>,
    modifier: Modifier = Modifier
) {
    val monthName = selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val year = selectedYearMonth.year
    val daysInMonth = selectedYearMonth.lengthOfMonth()

    val horizontalScrollState = rememberScrollState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
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
                        modifier = Modifier.testTag("prev_month_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = TextSecondaryDark
                        )
                    }

                    Text(
                        text = "$monthName $year",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )

                    IconButton(
                        onClick = { onYearMonthSelected(selectedYearMonth.plusMonths(1)) },
                        modifier = Modifier.testTag("next_month_button")
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

        // Habit Grid Section Item
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monthly Habit Grid",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Horizontal Scrollable Grid Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        // Days Header Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "Task",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AccentPurple
                                    )
                                )
                            }

                            for (day in 1..daysInMonth) {
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$day",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondaryDark
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = OutlineDark
                        )

                        // Task Grid Rows
                        gridRows.forEach { row ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = row.task.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimaryDark
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                row.dailyStatuses.forEach { dayStatus ->
                                    val symbol = when (dayStatus.status) {
                                        TaskStatus.COMPLETED -> "✓"
                                        TaskStatus.MISSED -> "✗"
                                        TaskStatus.NOT_SCHEDULED -> "—"
                                    }

                                    val bgColor = when (dayStatus.status) {
                                        TaskStatus.COMPLETED -> StatusGreen.copy(alpha = 0.15f)
                                        TaskStatus.MISSED -> StatusRed.copy(alpha = 0.15f)
                                        TaskStatus.NOT_SCHEDULED -> Color.Transparent
                                    }

                                    val textColor = when (dayStatus.status) {
                                        TaskStatus.COMPLETED -> StatusGreen
                                        TaskStatus.MISSED -> StatusRed
                                        TaskStatus.NOT_SCHEDULED -> TextTertiaryDark
                                    }

                                    Box(
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(28.dp)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(bgColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = symbol,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = textColor,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Grid Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GridLegendItem(symbol = "✓", label = "Completed", color = StatusGreen)
                        GridLegendItem(symbol = "✗", label = "Missed", color = StatusRed)
                        GridLegendItem(symbol = "—", label = "Not Scheduled", color = TextTertiaryDark)
                    }
                }
            }
        }

        // Task Consistency Section Header Item
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

        items(gridRows, key = { "stat_${it.task.id}" }) { row ->
            val stats = row.stats
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("consistency_card_${row.task.id}")
                    .border(1.dp, OutlineDark, RoundedCornerShape(14.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = row.task.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConsistencyMetric(label = "Completed", value = "${stats.completedDays}")
                        ConsistencyMetric(label = "Scheduled", value = "${stats.scheduledDays}")
                        ConsistencyMetric(label = "Missed", value = "${stats.missedDays}")
                        ConsistencyMetric(
                            label = "Consistency",
                            value = "${String.format(Locale.US, "%.1f", stats.consistencyPercentage)}%",
                            isHighlight = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridLegendItem(
    symbol: String,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondaryDark
            )
        )
    }
}

@Composable
private fun ConsistencyMetric(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondaryDark
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isHighlight) AccentPurple else TextPrimaryDark
            )
        )
    }
}
