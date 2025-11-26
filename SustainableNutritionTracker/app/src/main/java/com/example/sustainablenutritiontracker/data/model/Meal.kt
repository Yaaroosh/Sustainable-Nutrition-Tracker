package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val mealType: String,
    val imagePath: String? = null,
    val rating: Int = 0
)
