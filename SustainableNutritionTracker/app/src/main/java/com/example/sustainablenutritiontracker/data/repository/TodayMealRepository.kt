package com.example.sustainablenutritiontracker.data.repository

import com.example.sustainablenutritiontracker.data.database.TodayMealDao
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class TodayTotals(
    val calories: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val protein: Int = 0
)

class TodayMealRepository(
    private val todayMealDao: TodayMealDao
) {
    fun mealsForDate(date: String): Flow<List<TodayMealEntity>> =
        todayMealDao.getMealsForDate(date)

    suspend fun add(entity: TodayMealEntity) =
        todayMealDao.insertTodayMeal(entity)

    suspend fun delete(id: Long) =
        todayMealDao.deleteById(id)

    fun totals(date: String): Flow<TodayTotals> =
        combine(
            todayMealDao.totalCalories(date),
            todayMealDao.totalCarbs(date),
            todayMealDao.totalFat(date),
            todayMealDao.totalProtein(date)
        ) { cals, carbs, fat, protein ->
            TodayTotals(cals, carbs, fat, protein)
        }
}
