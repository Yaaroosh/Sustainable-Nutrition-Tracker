package com.example.sustainablenutritiontracker.data.repository

import com.example.sustainablenutritiontracker.data.database.TodayMealDao
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map  // ← FEHLT!

data class TodayTotals(
    val calories: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val protein: Int = 0
)

data class EnvironmentScore(
    val percentage: Int = 0,
    val veganCount: Int = 0,
    val vegetarianCount: Int = 0,
    val totalMeals: Int = 0
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

    fun environmentScore(date: String): Flow<EnvironmentScore> =
        mealsForDate(date).map { meals: List<TodayMealEntity> ->  // ← TYPE HINZUFÜGEN
            if (meals.isEmpty()) {
                EnvironmentScore(0)
            } else {
                val veganOrVegCount = meals.count { 
                    it.isVegan || it.vegetarian 
                }
                val percentage = (veganOrVegCount.toFloat() / meals.size * 100).toInt()
                EnvironmentScore(
                    percentage = percentage,
                    veganCount = veganOrVegCount,
                    vegetarianCount = meals.size - veganOrVegCount,
                    totalMeals = meals.size
                )
            }
        }
}
