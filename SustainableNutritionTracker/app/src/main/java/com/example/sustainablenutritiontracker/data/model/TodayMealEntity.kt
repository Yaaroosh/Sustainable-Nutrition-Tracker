package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "today_meals")
data class TodayMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // reference to template meal
    val mealId: Long,

    val title: String,
    val mealType: String,     // breakfast/lunch/dinner/snack
    val grams: Int,

    // stored as "consumed values" (already scaled by grams)
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int,

    // keep for filters (optional)
    val isVegan: Boolean = false,
    val containsMeat: Boolean = false,
    val vegetarian: Boolean = false,

    // yyyy-MM-dd
    val date: String
)
