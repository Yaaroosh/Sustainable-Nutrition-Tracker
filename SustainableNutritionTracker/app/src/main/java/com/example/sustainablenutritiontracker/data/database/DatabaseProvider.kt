package com.example.sustainablenutritiontracker.data.database

import android.content.Context
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import com.example.sustainablenutritiontracker.data.repository.SustainabilityRepository

object DatabaseProvider {

    fun provideRepository(context: Context): MealRepository {
        val db = AppDatabase.getDatabase(context)
        return MealRepository(
            mealDao = db.mealDao(),
            dailyGoalsDao = db.dailyGoalsDao()
        )
    }

    fun provideTodayRepository(context: Context): TodayMealRepository {
        val db = AppDatabase.getDatabase(context)
        return TodayMealRepository(
            todayMealDao = db.todayMealDao()
        )
    }

    // ✅ NEW
    fun provideSustainabilityRepository(context: Context): SustainabilityRepository {
        val db = AppDatabase.getDatabase(context)
        return SustainabilityRepository(db.sustainableDayDao())
    }
}
