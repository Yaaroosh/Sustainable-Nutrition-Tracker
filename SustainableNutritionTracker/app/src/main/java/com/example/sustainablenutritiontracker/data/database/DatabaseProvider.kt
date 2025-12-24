package com.example.sustainablenutritiontracker.data.database

import android.content.Context
import androidx.room.Room
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository

object DatabaseProvider {

    fun provideRepository(context: Context): MealRepository {
        val db = AppDatabase.getDatabase(context)

        return MealRepository(
            db.mealDao(),
            db.dailyGoalsDao()
        )
    }

    fun provideTodayMealRepository(context: Context): TodayMealRepository {
        val db = AppDatabase.getDatabase(context)

        return TodayMealRepository(
            db.todayMealDao(),
        )
    }
}
