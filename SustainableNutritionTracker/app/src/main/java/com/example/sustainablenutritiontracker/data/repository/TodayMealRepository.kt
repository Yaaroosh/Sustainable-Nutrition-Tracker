package com.example.sustainablenutritiontracker.data.repository

import com.example.sustainablenutritiontracker.data.database.TodayMealDao
import com.example.sustainablenutritiontracker.data.database.TodayMealEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TodayMealRepository(
    private val todayMealDao: TodayMealDao
) {

    private fun today(): String =
        LocalDate.now().toString()

    fun getTodayMeals(): Flow<List<TodayMealEntity>> {
        return todayMealDao.getTodayMeals(today())
    }

    suspend fun addMeal(mealId: Long) {
        todayMealDao.insertTodayMeal(
            TodayMealEntity(
                mealId = mealId,
                date = today()
            )
        )
    }

    suspend fun deleteMeal(todayMealId: Long) {
        todayMealDao.deleteTodayMeal(todayMealId)
    }

    suspend fun dailyReset() {
        todayMealDao.deleteOldMeals(today())
    }
}
