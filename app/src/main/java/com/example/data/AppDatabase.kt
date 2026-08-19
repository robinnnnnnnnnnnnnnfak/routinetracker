package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.TaskCompletionDao
import com.example.data.dao.TaskDao
import com.example.data.model.TaskCompletionEntity
import com.example.data.model.TaskEntity

@Database(
    entities = [TaskEntity::class, TaskCompletionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun taskCompletionDao(): TaskCompletionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "routine_tracker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
