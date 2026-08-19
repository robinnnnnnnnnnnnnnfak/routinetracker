package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import com.example.data.model.TaskEntity
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurplePrimary
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TextTertiaryDark
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskManagementScreen(
    tasks: List<TaskEntity>,
    onAddTask: (title: String, scheduledDays: Set<DayOfWeek>) -> Unit,
    onUpdateTask: (task: TaskEntity, title: String, scheduledDays: Set<DayOfWeek>) -> Unit,
    onDeleteTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var deletingTaskId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentPurplePrimary,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_task_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
        ) {
            item {
                Text(
                    text = "Task Management",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                )
            }

            item {
                Text(
                    text = "Define your target tasks and choose which days of the week they are scheduled.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondaryDark
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            items(tasks, key = { it.id }) { task ->
                TaskManagementCard(
                    task = task,
                    onEdit = { editingTask = task },
                    onDelete = { deletingTaskId = task.id }
                )
            }
        }
    }

    if (showAddDialog) {
        TaskDialog(
            title = "Add New Task",
            initialTitle = "",
            initialScheduledDays = DayOfWeek.values().toSet(),
            onDismiss = { showAddDialog = false },
            onConfirm = { newTitle, days ->
                if (newTitle.isNotBlank() && days.isNotEmpty()) {
                    onAddTask(newTitle, days)
                    showAddDialog = false
                }
            }
        )
    }

    editingTask?.let { task ->
        TaskDialog(
            title = "Edit Task",
            initialTitle = task.title,
            initialScheduledDays = task.getScheduledDaysSet(),
            onDismiss = { editingTask = null },
            onConfirm = { newTitle, days ->
                if (newTitle.isNotBlank() && days.isNotEmpty()) {
                    onUpdateTask(task, newTitle, days)
                    editingTask = null
                }
            }
        )
    }

    deletingTaskId?.let { taskId ->
        AlertDialog(
            onDismissRequest = { deletingTaskId = null },
            containerColor = SurfaceDark,
            titleContentColor = TextPrimaryDark,
            textContentColor = TextSecondaryDark,
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task? All history for this task will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(taskId)
                        deletingTaskId = null
                    }
                ) {
                    Text("Delete", color = StatusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingTaskId = null }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskManagementCard(
    task: TaskEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val scheduledDays = task.getScheduledDaysSet()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("management_task_card_${task.id}")
            .border(1.dp, OutlineDark, RoundedCornerShape(16.dp)),
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    ),
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.testTag("edit_task_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = AccentPurple
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_task_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = StatusRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Scheduled Days:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondaryDark
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DayOfWeek.values().forEach { day ->
                    val isScheduled = scheduledDays.contains(day)
                    val shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isScheduled) AccentPurple.copy(alpha = 0.2f)
                                else SurfaceElevatedDark
                            )
                            .border(
                                width = 1.dp,
                                color = if (isScheduled) AccentPurple.copy(alpha = 0.4f) else OutlineDark,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = shortName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isScheduled) AccentPurple
                                else TextTertiaryDark
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskDialog(
    title: String,
    initialTitle: String,
    initialScheduledDays: Set<DayOfWeek>,
    onDismiss: () -> Unit,
    onConfirm: (String, Set<DayOfWeek>) -> Unit
) {
    var taskTitle by remember { mutableStateOf(initialTitle) }
    var selectedDays by remember { mutableStateOf(initialScheduledDays) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        titleContentColor = TextPrimaryDark,
        textContentColor = TextSecondaryDark,
        title = { Text(title, color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input")
                )

                Text(
                    text = "Scheduled Days:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DayOfWeek.values().forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        val shortName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            },
                            label = { Text(shortName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple.copy(alpha = 0.25f),
                                selectedLabelColor = AccentPurple,
                                containerColor = SurfaceElevatedDark,
                                labelColor = TextSecondaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AccentPurple else OutlineDark
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(taskTitle, selectedDays) },
                enabled = taskTitle.isNotBlank() && selectedDays.isNotEmpty(),
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text("Save", color = AccentPurple, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
