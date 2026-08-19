package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val scheduledDaysMask: Int = ALL_DAYS_MASK, // Bitmask for Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val displayOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isScheduledOn(dayOfWeek: DayOfWeek): Boolean {
        val bit = 1 shl (dayOfWeek.value - 1)
        return (scheduledDaysMask and bit) != 0
    }

    fun getScheduledDaysSet(): Set<DayOfWeek> {
        return getScheduledDays(scheduledDaysMask)
    }

    companion object {
        const val ALL_DAYS_MASK = 127 // 1 + 2 + 4 + 8 + 16 + 32 + 64
        
        fun createMask(days: Set<DayOfWeek>): Int {
            var mask = 0
            for (day in days) {
                mask = mask or (1 shl (day.value - 1))
            }
            return mask
        }

        fun getScheduledDays(mask: Int): Set<DayOfWeek> {
            val set = mutableSetOf<DayOfWeek>()
            for (day in DayOfWeek.values()) {
                val bit = 1 shl (day.value - 1)
                if ((mask and bit) != 0) {
                    set.add(day)
                }
            }
            return set
        }
    }
}
