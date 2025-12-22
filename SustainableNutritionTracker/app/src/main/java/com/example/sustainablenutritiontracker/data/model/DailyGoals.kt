package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoals(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    val caloriesLimit: Int = 2000,
    val carbsLimit: Int = 250,
    val fatLimit: Int = 70,
    val proteinLimit: Int = 150
)
