package com.example.sustainablenutritiontracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sustainablenutritiontracker.data.model.Meal

@Database(entities = [Meal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
}