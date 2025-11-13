package com.divinebudget.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val dailyContribution: Double = 0.0,
    val createdAt: Date = Date(),
    val completedAt: Date? = null,
    val isCompleted: Boolean = false
)

