package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurplePrimary
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark

import com.example.ui.model.DailySummary
import com.example.ui.model.TaskDailyItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    summary: DailySummary,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onToggleTask: (taskId: Long, isCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isActualToday = selectedDate == LocalDate.now()
    val formattedDate = selectedDate.format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    )

    val scheduledItems = summary.items.filter { it.isScheduled }
    val unscheduledItems = summary.items.filter { !it.isScheduled }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Date Selector Header Item
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onDateSelected(selectedDate.minusDays(1)) },
                    modifier = Modifier.testTag("prev_date_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Day",
                        tint = TextSecondaryDark
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (isActualToday) "Today" else selectedDate.dayOfWeek.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondaryDark
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isActualToday) {
                        IconButton(
                            onClick = { onDateSelected(LocalDate.now()) },
                            modifier = Modifier.testTag("today_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Jump to Today",
                                tint = AccentPurple
                            )
                        }
                    }

                    IconButton(
                        onClick = { onDateSelected(selectedDate.plusDays(1)) },
                        modifier = Modifier.testTag("next_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Day",
                            tint = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Daily Summary Card Item
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_summary_card")
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    // Header inside card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Summary",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3 Metric columns with vertical dividers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryMetricItem(
                            label = "Completed",
                            value = "${summary.completedCount} / ${summary.totalScheduled}",
                            color = AccentPurple
                        )

                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = OutlineDark
                        )

                        SummaryMetricItem(
                            label = "Missed",
                            value = "${summary.missedCount}",
                            color = AccentPurple
                        )

                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = OutlineDark
                        )

                        SummaryMetricItem(
                            label = "Daily Completion",
                            value = "${String.format(Locale.US, "%.0f", summary.completionPercentage)}%",
                            color = AccentPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress row with percentage text on right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { (summary.completionPercentage / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AccentPurplePrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = "${String.format(Locale.US, "%.0f", summary.completionPercentage)}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasks for Today",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                )

                Text(
                    text = "${summary.completedCount} / ${summary.totalScheduled} Completed",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AccentPurple
                    )
                )
            }
        }

        if (scheduledItems.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, OutlineDark, RoundedCornerShape(14.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "No tasks scheduled for this day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(scheduledItems, key = { it.task.id }) { item ->
                TaskDailyRow(
                    item = item,
                    onToggleTask = { isChecked ->
                        onToggleTask(item.task.id, isChecked)
                    }
                )
            }
        }

        if (unscheduledItems.isNotEmpty()) {
            item {
                Text(
                    text = "Not Scheduled Today",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextTertiaryDark,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            items(unscheduledItems, key = { "unscheduled_${it.task.id}" }) { item ->
                TaskDailyRow(
                    item = item,
                    onToggleTask = { /* Read only */ }
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricItem(
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
fun TaskDailyRow(
    item: TaskDailyItem,
    onToggleTask: (Boolean) -> Unit
) {
    val isScheduled = item.isScheduled
    val isCompleted = item.isCompleted

    val containerColor = MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_item_${item.task.id}")
            .border(1.dp, OutlineDark, RoundedCornerShape(14.dp))
            .clickable(enabled = isScheduled) {
                onToggleTask(!isCompleted)
            },
        color = containerColor,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isScheduled) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleTask(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AccentPurplePrimary,
                        uncheckedColor = OutlineDark,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.testTag("checkbox_task_${item.task.id}")
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextTertiaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.None, // Absolute NO strikethrough!
                        color = if (!isScheduled) TextTertiaryDark else TextPrimaryDark
                    )
                )
            }

            if (isScheduled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (isCompleted) AccentPurple else TextTertiaryDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCompleted) "Completed" else "Pending",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCompleted) AccentPurple else TextSecondaryDark,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            } else {
                Text(
                    text = "Not Scheduled",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextTertiaryDark
                    )
                )
            }
        }
    }
}
