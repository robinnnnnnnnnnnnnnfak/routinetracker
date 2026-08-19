package com.example.ui.model

import com.example.data.model.TaskEntity
import java.time.LocalDate
import java.time.YearMonth

enum class TaskStatus {
    COMPLETED,     // ✓
    MISSED,        // ✗
    NOT_SCHEDULED  // —
}

data class TaskDailyItem(
    val task: TaskEntity,
    val isScheduled: Boolean,
    val isCompleted: Boolean,
    val status: TaskStatus
)

data class DailySummary(
    val date: LocalDate,
    val totalScheduled: Int,
    val completedCount: Int,
    val missedCount: Int,
    val completionPercentage: Double,
    val items: List<TaskDailyItem>
) {
    val missedTaskTitles: List<String>
        get() = items.filter { it.status == TaskStatus.MISSED }.map { it.task.title }
}

data class TaskMonthlyStats(
    val task: TaskEntity,
    val completedDays: Int,
    val scheduledDays: Int,
    val missedDays: Int,
    val consistencyPercentage: Double
)

data class DayGridStatus(
    val dayOfMonth: Int,
    val date: LocalDate,
    val status: TaskStatus
)

data class TaskGridRow(
    val task: TaskEntity,
    val dailyStatuses: List<DayGridStatus>,
    val stats: TaskMonthlyStats
)

data class TrendAnalysis(
    val earlierPeriodAvg: Double,
    val laterPeriodAvg: Double,
    val percentagePointChange: Double,
    val statement: String,
    val hasEnoughData: Boolean = true
)

data class MonthComparison(
    val currentMonthName: String,
    val currentMonthPct: Double,
    val previousMonthName: String,
    val previousMonthPct: Double,
    val percentagePointChange: Double,
    val hasPreviousData: Boolean
)

data class MonthlyReport(
    val yearMonth: YearMonth,
    val overallCompletionPercentage: Double,
    val totalScheduledOccurrences: Int,
    val totalCompletedOccurrences: Int,
    val totalMissedOccurrences: Int,
    val taskStats: List<TaskMonthlyStats>,
    val bestDay: DailySummary?,
    val worstDay: DailySummary?,
    val dailyPoints: List<DailySummary> = emptyList(),
    val trendAnalysis: TrendAnalysis,
    val monthComparison: MonthComparison
)
