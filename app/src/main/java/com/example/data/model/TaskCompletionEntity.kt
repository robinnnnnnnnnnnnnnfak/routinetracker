package com.example.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "task_completions",
    primaryKeys = ["taskId", "date"],
    indices = [Index(value = ["date"])]
)
data class TaskCompletionEntity(
    val taskId: Long,
    val date: String, // ISO format "YYYY-MM-DD" e.g. "2026-08-13"
    val isCompleted: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
