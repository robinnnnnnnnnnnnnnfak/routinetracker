package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MonthlyScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.TaskManagementScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.AccentContainerDark
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextTertiaryDark
import com.example.ui.viewmodel.RoutineViewModel

enum class NavigationTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    TODAY("Today", Icons.Default.Today, "tab_today"),
    HISTORY("History", Icons.Default.History, "tab_history"),
    MONTHLY("Monthly", Icons.Default.CalendarMonth, "tab_monthly"),
    REPORTS("Reports", Icons.Default.Assessment, "tab_reports"),
    TASKS("Tasks", Icons.Default.FormatListNumbered, "tab_tasks")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineApp(viewModel: RoutineViewModel) {
    var selectedTab by remember { mutableStateOf(NavigationTab.TODAY) }
    val configuration = LocalConfiguration.current
    val isExpandedWidth = configuration.screenWidthDp >= 600

    val todaySummary by viewModel.todaySummary.collectAsStateWithLifecycle()
    val historySummary by viewModel.historySummary.collectAsStateWithLifecycle()
    val selectedTodayDate by viewModel.selectedTodayDate.collectAsStateWithLifecycle()
    val selectedHistoryDate by viewModel.selectedHistoryDate.collectAsStateWithLifecycle()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val monthlyGridRows by viewModel.monthlyGridRows.collectAsStateWithLifecycle()
    val monthlyReport by viewModel.monthlyReport.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = AccentPurple,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                ) {
                                    append("Maxx")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = TextPrimaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" Routine")
                                }
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menu action */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (!isExpandedWidth) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp
                ) {
                    NavigationTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = com.example.ui.theme.AccentPurple,
                                selectedTextColor = com.example.ui.theme.AccentPurple,
                                indicatorColor = com.example.ui.theme.AccentContainerDark,
                                unselectedIconColor = com.example.ui.theme.TextTertiaryDark,
                                unselectedTextColor = com.example.ui.theme.TextTertiaryDark
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isExpandedWidth) {
                NavigationRail {
                    NavigationTab.values().forEach { tab ->
                        NavigationRailItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    NavigationTab.TODAY -> {
                        TodayScreen(
                            summary = todaySummary,
                            selectedDate = selectedTodayDate,
                            onDateSelected = { viewModel.setSelectedTodayDate(it) },
                            onToggleTask = { taskId, isCompleted ->
                                viewModel.toggleTaskCompletion(taskId, selectedTodayDate, isCompleted)
                            }
                        )
                    }
                    NavigationTab.HISTORY -> {
                        HistoryScreen(
                            summary = historySummary,
                            selectedDate = selectedHistoryDate,
                            onDateSelected = { viewModel.setSelectedHistoryDate(it) },
                            onToggleTask = { taskId, isCompleted ->
                                viewModel.toggleTaskCompletion(taskId, selectedHistoryDate, isCompleted)
                            }
                        )
                    }
                    NavigationTab.MONTHLY -> {
                        MonthlyScreen(
                            selectedYearMonth = selectedYearMonth,
                            onYearMonthSelected = { viewModel.setSelectedYearMonth(it) },
                            gridRows = monthlyGridRows
                        )
                    }
                    NavigationTab.REPORTS -> {
                        ReportsScreen(
                            report = monthlyReport,
                            selectedYearMonth = selectedYearMonth,
                            onYearMonthSelected = { viewModel.setSelectedYearMonth(it) }
                        )
                    }
                    NavigationTab.TASKS -> {
                        TaskManagementScreen(
                            tasks = tasks,
                            onAddTask = { title, scheduledDays ->
                                viewModel.addTask(title, scheduledDays)
                            },
                            onUpdateTask = { task, title, scheduledDays ->
                                viewModel.updateTask(task, title, scheduledDays)
                            },
                            onDeleteTask = { taskId ->
                                viewModel.deleteTask(taskId)
                            }
                        )
                    }
                }
            }
        }
    }
}
