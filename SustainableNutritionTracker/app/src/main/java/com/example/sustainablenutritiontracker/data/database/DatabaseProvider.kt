package com.example.sustainablenutritiontracker.data.database

import android.content.Context
import androidx.room.Room
import com.example.sustainablenutritiontracker.data.repository.MealRepository

object DatabaseProvider {

    fun provideRepository(context: Context): MealRepository {
        val db = AppDatabase.getDatabase(context)

        // ← HIER ÄNDERN: Beide DAOs übergeben
        return MealRepository(
            db.mealDao(),
            db.dailyGoalsDao()
        )
    }
}
