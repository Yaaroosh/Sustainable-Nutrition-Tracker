package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "today_meals")
data class TodayMealEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,


    val mealId: Long,

    val title: String? = null,
    val calories: Int? = null,
    val protein: Int? = null,
    val carbs: Int? = null,
    val fat: Int? = null,
    val mealType: String? = null,

    // Optional: Zeitstempel (z. B. für spätere Erweiterungen)
    val timestamp: Long = System.currentTimeMillis(),
    val date: String
)
