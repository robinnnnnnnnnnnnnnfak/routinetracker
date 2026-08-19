package com.example.data

import com.example.data.dao.TaskCompletionDao
import com.example.data.dao.TaskDao
import com.example.data.model.TaskCompletionEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class RoutineRepository(
    private val taskDao: TaskDao,
    private val taskCompletionDao: TaskCompletionDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allCompletions: Flow<List<TaskCompletionEntity>> = taskCompletionDao.getAllCompletions()

    suspend fun checkAndSeedInitialTasks() {
        val existingTasks = taskDao.getAllTasksSync()
        if (existingTasks.isEmpty()) {
            val initialTasks = listOf(
                TaskEntity(
                    title = "Study Computer (2 hours)",
                    scheduledDaysMask = TaskEntity.ALL_DAYS_MASK,
                    displayOrder = 1
                ),
                TaskEntity(
                    title = "Learn Hindi (1 hour)",
                    scheduledDaysMask = TaskEntity.ALL_DAYS_MASK,
                    displayOrder = 2
                ),
                TaskEntity(
                    title = "Self-Study (1 hour)",
                    scheduledDaysMask = TaskEntity.ALL_DAYS_MASK,
                    displayOrder = 3
                ),
                TaskEntity(
                    title = "Road to Millions (1.5 hours)",
                    scheduledDaysMask = TaskEntity.ALL_DAYS_MASK,
                    displayOrder = 4
                )
            )
            taskDao.insertTasks(initialTasks)
        }
    }

    fun getCompletionsForDate(date: String): Flow<List<TaskCompletionEntity>> {
        return taskCompletionDao.getCompletionsForDate(date)
    }

    fun getCompletionsForRange(startDate: String, endDate: String): Flow<List<TaskCompletionEntity>> {
        return taskCompletionDao.getCompletionsForRange(startDate, endDate)
    }

    suspend fun toggleTaskCompletion(taskId: Long, date: String, isCompleted: Boolean) {
        if (isCompleted) {
            taskCompletionDao.insertCompletion(
                TaskCompletionEntity(taskId = taskId, date = date, isCompleted = true)
            )
        } else {
            taskCompletionDao.deleteCompletion(taskId = taskId, date = date)
        }
    }

    suspend fun addTask(title: String, scheduledDaysMask: Int) {
        val tasks = taskDao.getAllTasksSync()
        val nextOrder = (tasks.maxOfOrNull { it.displayOrder } ?: 0) + 1
        taskDao.insertTask(
            TaskEntity(
                title = title.trim(),
                scheduledDaysMask = scheduledDaysMask,
                displayOrder = nextOrder
            )
        )
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(taskId: Long) {
        taskCompletionDao.deleteCompletionsForTask(taskId)
        taskDao.deleteTaskById(taskId)
    }
}
