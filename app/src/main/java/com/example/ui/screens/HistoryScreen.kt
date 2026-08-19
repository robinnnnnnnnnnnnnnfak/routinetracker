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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.ui.model.DailySummary
import com.example.ui.model.TaskDailyItem
import com.example.ui.model.TaskStatus
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurplePrimary
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    summary: DailySummary,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onToggleTask: (taskId: Long, isCompleted: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val formattedDate = selectedDate.format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    )

    if (showDatePickerDialog) {
        val initialZoneMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialZoneMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onDateSelected(pickedDate)
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("Select Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Date Header Control Item
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
                        onClick = { onDateSelected(selectedDate.minusDays(1)) },
                        modifier = Modifier.testTag("history_prev_date")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Day",
                            tint = TextSecondaryDark
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDatePickerDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = AccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            )
                            Text(
                                text = "Tap to pick date",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AccentPurple
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { onDateSelected(selectedDate.plusDays(1)) },
                        modifier = Modifier.testTag("history_next_date")
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

        // Summary Card Item
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HistoryMetric(
                        label = "Completed",
                        value = "${summary.completedCount} / ${summary.totalScheduled}",
                        color = AccentPurple
                    )
                    VerticalDivider(
                        modifier = Modifier.height(36.dp),
                        color = OutlineDark
                    )
                    HistoryMetric(
                        label = "Missed",
                        value = "${summary.missedCount}",
                        color = AccentPurple
                    )
                    VerticalDivider(
                        modifier = Modifier.height(36.dp),
                        color = OutlineDark
                    )
                    HistoryMetric(
                        label = "Completion",
                        value = "${String.format(Locale.US, "%.0f", summary.completionPercentage)}%",
                        color = AccentPurple
                    )
                }
            }
        }

        item {
            Text(
                text = "Recorded Status for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                ),
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )
        }

        items(summary.items, key = { it.task.id }) { item ->
            HistoryTaskRow(
                item = item,
                onToggleTask = { isChecked ->
                    onToggleTask(item.task.id, isChecked)
                }
            )
        }
    }
}

@Composable
private fun HistoryMetric(
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
fun HistoryTaskRow(
    item: TaskDailyItem,
    onToggleTask: (Boolean) -> Unit
) {
    val statusIcon = when (item.status) {
        TaskStatus.COMPLETED -> "✓"
        TaskStatus.MISSED -> "✗"
        TaskStatus.NOT_SCHEDULED -> "—"
    }

    val statusColor = when (item.status) {
        TaskStatus.COMPLETED -> StatusGreen
        TaskStatus.MISSED -> StatusRed
        TaskStatus.NOT_SCHEDULED -> TextTertiaryDark
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.task.id}")
            .border(1.dp, OutlineDark, RoundedCornerShape(14.dp))
            .clickable(enabled = item.isScheduled) {
                onToggleTask(!item.isCompleted)
            },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusIcon,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.None, // NO strikethrough!
                        color = if (item.isScheduled) TextPrimaryDark else TextTertiaryDark
                    )
                )
            }

            Text(
                text = when (item.status) {
                    TaskStatus.COMPLETED -> "Completed"
                    TaskStatus.MISSED -> "Missed"
                    TaskStatus.NOT_SCHEDULED -> "Not Scheduled"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = statusColor
                )
            )
        }
    }
}
