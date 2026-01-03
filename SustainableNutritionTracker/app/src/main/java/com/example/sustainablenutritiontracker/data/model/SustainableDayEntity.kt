package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sustainable_days")
data class SustainableDayEntity(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val environmentScore: Int,
    val isSustainable: Boolean
)
