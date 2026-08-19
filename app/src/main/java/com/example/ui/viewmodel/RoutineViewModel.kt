package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.RoutineRepository
import com.example.data.model.TaskCompletionEntity
import com.example.data.model.TaskEntity
import com.example.ui.model.DailySummary
import com.example.ui.model.DayGridStatus
import com.example.ui.model.MonthComparison
import com.example.ui.model.MonthlyReport
import com.example.ui.model.TaskDailyItem
import com.example.ui.model.TaskGridRow
import com.example.ui.model.TaskMonthlyStats
import com.example.ui.model.TaskStatus
import com.example.ui.model.TrendAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class RoutineViewModel(private val repository: RoutineRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialTasks()
        }
    }

    val selectedTodayDate = MutableStateFlow(LocalDate.now())
    val selectedHistoryDate = MutableStateFlow(LocalDate.now())
    val selectedYearMonth = MutableStateFlow(YearMonth.now())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completions: StateFlow<List<TaskCompletionEntity>> = repository.allCompletions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todaySummary: StateFlow<DailySummary> = combine(
        tasks,
        completions,
        selectedTodayDate
    ) { allTasks, allCompletions, date ->
        calculateDailySummary(allTasks, allCompletions, date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = calculateDailySummary(emptyList(), emptyList(), LocalDate.now())
    )

    val historySummary: StateFlow<DailySummary> = combine(
        tasks,
        completions,
        selectedHistoryDate
    ) { allTasks, allCompletions, date ->
        calculateDailySummary(allTasks, allCompletions, date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = calculateDailySummary(emptyList(), emptyList(), LocalDate.now())
    )

    val monthlyGridRows: StateFlow<List<TaskGridRow>> = combine(
        tasks,
        completions,
        selectedYearMonth
    ) { allTasks, allCompletions, ym ->
        calculateMonthlyGrid(allTasks, allCompletions, ym)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val monthlyReport: StateFlow<MonthlyReport> = combine(
        tasks,
        completions,
        selectedYearMonth
    ) { allTasks, allCompletions, ym ->
        calculateMonthlyReport(allTasks, allCompletions, ym)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = calculateMonthlyReport(emptyList(), emptyList(), YearMonth.now())
    )

    fun toggleTaskCompletion(taskId: Long, date: LocalDate, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, date.toString(), isCompleted)
        }
    }

    fun setSelectedTodayDate(date: LocalDate) {
        selectedTodayDate.value = date
    }

    fun setSelectedHistoryDate(date: LocalDate) {
        selectedHistoryDate.value = date
    }

    fun setSelectedYearMonth(yearMonth: YearMonth) {
        selectedYearMonth.value = yearMonth
    }

    fun addTask(title: String, scheduledDays: Set<DayOfWeek>) {
        viewModelScope.launch {
            val mask = TaskEntity.createMask(scheduledDays)
            repository.addTask(title, mask)
        }
    }

    fun updateTask(task: TaskEntity, newTitle: String, scheduledDays: Set<DayOfWeek>) {
        viewModelScope.launch {
            val mask = TaskEntity.createMask(scheduledDays)
            val updated = task.copy(
                title = newTitle.trim(),
                scheduledDaysMask = mask
            )
            repository.updateTask(updated)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    private fun calculateDailySummary(
        tasks: List<TaskEntity>,
        completions: List<TaskCompletionEntity>,
        date: LocalDate
    ): DailySummary {
        val dateStr = date.toString()
        val dayOfWeek = date.dayOfWeek
        val completionMap = completions.filter { it.date == dateStr }
            .associateBy { it.taskId }

        val items = tasks.map { task ->
            val isScheduled = task.isScheduledOn(dayOfWeek)
            val isCompleted = completionMap[task.id]?.isCompleted == true
            val status = when {
                !isScheduled -> TaskStatus.NOT_SCHEDULED
                isCompleted -> TaskStatus.COMPLETED
                else -> TaskStatus.MISSED
            }
            TaskDailyItem(
                task = task,
                isScheduled = isScheduled,
                isCompleted = isCompleted,
                status = status
            )
        }

        val scheduledItems = items.filter { it.isScheduled }
        val totalScheduled = scheduledItems.size
        val completedCount = scheduledItems.count { it.isCompleted }
        val missedCount = totalScheduled - completedCount
        val completionPercentage = if (totalScheduled > 0) {
            (completedCount.toDouble() / totalScheduled) * 100.0
        } else {
            0.0
        }

        return DailySummary(
            date = date,
            totalScheduled = totalScheduled,
            completedCount = completedCount,
            missedCount = missedCount,
            completionPercentage = completionPercentage,
            items = items
        )
    }

    private fun calculateMonthlyGrid(
        tasks: List<TaskEntity>,
        completions: List<TaskCompletionEntity>,
        ym: YearMonth
    ): List<TaskGridRow> {
        val daysInMonth = ym.lengthOfMonth()
        val completionMap = completions.associateBy { "${it.taskId}_${it.date}" }

        return tasks.map { task ->
            var completedDays = 0
            var scheduledDays = 0
            val dailyStatuses = mutableListOf<DayGridStatus>()

            for (day in 1..daysInMonth) {
                val date = ym.atDay(day)
                val isScheduled = task.isScheduledOn(date.dayOfWeek)
                val key = "${task.id}_$date"
                val isCompleted = completionMap[key]?.isCompleted == true

                val status = when {
                    !isScheduled -> TaskStatus.NOT_SCHEDULED
                    isCompleted -> TaskStatus.COMPLETED
                    else -> TaskStatus.MISSED
                }

                if (isScheduled) {
                    scheduledDays++
                    if (isCompleted) {
                        completedDays++
                    }
                }

                dailyStatuses.add(
                    DayGridStatus(
                        dayOfMonth = day,
                        date = date,
                        status = status
                    )
                )
            }

            val missedDays = scheduledDays - completedDays
            val consistencyPct = if (scheduledDays > 0) {
                (completedDays.toDouble() / scheduledDays) * 100.0
            } else {
                0.0
            }

            TaskGridRow(
                task = task,
                dailyStatuses = dailyStatuses,
                stats = TaskMonthlyStats(
                    task = task,
                    completedDays = completedDays,
                    scheduledDays = scheduledDays,
                    missedDays = missedDays,
                    consistencyPercentage = consistencyPct
                )
            )
        }
    }

    private fun calculateMonthlyReport(
        tasks: List<TaskEntity>,
        completions: List<TaskCompletionEntity>,
        ym: YearMonth
    ): MonthlyReport {
        val gridRows = calculateMonthlyGrid(tasks, completions, ym)
        val taskStats = gridRows.map { it.stats }

        val totalScheduledOccurrences = taskStats.sumOf { it.scheduledDays }
        val totalCompletedOccurrences = taskStats.sumOf { it.completedDays }
        val totalMissedOccurrences = totalScheduledOccurrences - totalCompletedOccurrences
        val overallCompletionPercentage = if (totalScheduledOccurrences > 0) {
            (totalCompletedOccurrences.toDouble() / totalScheduledOccurrences) * 100.0
        } else {
            0.0
        }

        // Daily summaries for each day of the month that has scheduled tasks
        val daysInMonth = ym.lengthOfMonth()
        val dailySummariesInMonth = mutableListOf<DailySummary>()
        for (day in 1..daysInMonth) {
            val date = ym.atDay(day)
            val summary = calculateDailySummary(tasks, completions, date)
            if (summary.totalScheduled > 0) {
                dailySummariesInMonth.add(summary)
            }
        }

        // Best and Worst Day
        val bestDay = dailySummariesInMonth.maxByOrNull { it.completionPercentage }
        val worstDay = dailySummariesInMonth.minByOrNull { it.completionPercentage }

        // Trend Analysis
        val trendAnalysis = calculateTrendAnalysis(tasks, completions, ym)

        // Month-to-Month Comparison
        val prevMonth = ym.minusMonths(1)
        val prevGridRows = calculateMonthlyGrid(tasks, completions, prevMonth)
        val prevTaskStats = prevGridRows.map { it.stats }
        val prevScheduledOccurrences = prevTaskStats.sumOf { it.scheduledDays }
        val prevCompletedOccurrences = prevTaskStats.sumOf { it.completedDays }
        val hasPrevData = prevScheduledOccurrences > 0 && completions.any {
            try {
                val d = LocalDate.parse(it.date)
                d.year == prevMonth.year && d.month == prevMonth.month
            } catch (e: Exception) {
                false
            }
        }
        val prevOverallPct = if (prevScheduledOccurrences > 0) {
            (prevCompletedOccurrences.toDouble() / prevScheduledOccurrences) * 100.0
        } else {
            0.0
        }

        val diff = overallCompletionPercentage - prevOverallPct
        val currentMonthName = ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + ym.year
        val prevMonthName = prevMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + prevMonth.year

        val monthComparison = MonthComparison(
            currentMonthName = currentMonthName,
            currentMonthPct = overallCompletionPercentage,
            previousMonthName = prevMonthName,
            previousMonthPct = prevOverallPct,
            percentagePointChange = diff,
            hasPreviousData = hasPrevData
        )

        return MonthlyReport(
            yearMonth = ym,
            overallCompletionPercentage = overallCompletionPercentage,
            totalScheduledOccurrences = totalScheduledOccurrences,
            totalCompletedOccurrences = totalCompletedOccurrences,
            totalMissedOccurrences = totalMissedOccurrences,
            taskStats = taskStats,
            bestDay = bestDay,
            worstDay = worstDay,
            dailyPoints = dailySummariesInMonth,
            trendAnalysis = trendAnalysis,
            monthComparison = monthComparison
        )
    }

    private fun calculateTrendAnalysis(
        tasks: List<TaskEntity>,
        completions: List<TaskCompletionEntity>,
        currentYm: YearMonth
    ): TrendAnalysis {
        // Collect all daily summaries up to the end of selected month (or current date)
        val endDate = currentYm.atEndOfMonth()
        // Collect recorded dates from completions
        val datesWithData = completions.mapNotNull {
            try { LocalDate.parse(it.date) } catch (e: Exception) { null }
        }.filter { !it.isAfter(endDate) }.toSet()

        val datesToEvaluate = if (datesWithData.isNotEmpty()) {
            datesWithData.sorted()
        } else {
            // Default to days in current month up to now
            val daysInMonth = currentYm.lengthOfMonth()
            (1..daysInMonth).map { currentYm.atDay(it) }
        }

        val summaries = datesToEvaluate.map { calculateDailySummary(tasks, completions, it) }
            .filter { it.totalScheduled > 0 }

        if (summaries.size < 2) {
            return TrendAnalysis(
                earlierPeriodAvg = 0.0,
                laterPeriodAvg = 0.0,
                percentagePointChange = 0.0,
                statement = "Your performance is relatively stable.",
                hasEnoughData = false
            )
        }

        val halfIndex = summaries.size / 2
        val earlierPeriod = summaries.subList(0, halfIndex)
        val laterPeriod = summaries.subList(halfIndex, summaries.size)

        val earlierAvg = earlierPeriod.map { it.completionPercentage }.average()
        val laterAvg = laterPeriod.map { it.completionPercentage }.average()
        val diff = laterAvg - earlierAvg

        val statement = when {
            diff > 2.0 -> "Your performance is improving."
            diff < -2.0 -> "Your performance is decreasing."
            else -> "Your performance is relatively stable."
        }

        return TrendAnalysis(
            earlierPeriodAvg = earlierAvg,
            laterPeriodAvg = laterAvg,
            percentagePointChange = diff,
            statement = statement,
            hasEnoughData = true
        )
    }
}

class RoutineViewModelFactory(
    private val repository: RoutineRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            return RoutineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
