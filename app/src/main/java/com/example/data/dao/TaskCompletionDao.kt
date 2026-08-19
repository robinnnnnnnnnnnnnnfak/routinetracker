package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TaskCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCompletionDao {
    @Query("SELECT * FROM task_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions WHERE date = :date")
    suspend fun getCompletionsForDateSync(date: String): List<TaskCompletionEntity>

    @Query("SELECT * FROM task_completions WHERE date BETWEEN :startDate AND :endDate")
    fun getCompletionsForRange(startDate: String, endDate: String): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM task_completions WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCompletionsForRangeSync(startDate: String, endDate: String): List<TaskCompletionEntity>

    @Query("SELECT * FROM task_completions")
    fun getAllCompletions(): Flow<List<TaskCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: TaskCompletionEntity)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND date = :date")
    suspend fun deleteCompletion(taskId: Long, date: String)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId")
    suspend fun deleteCompletionsForTask(taskId: Long)
}
