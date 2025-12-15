package com.example.sustainablenutritiontracker.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.sustainablenutritiontracker.data.database.MealDao
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.NutritionTotals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import com.example.sustainablenutritiontracker.data.model.toLocalDate

class MealRepository(
    private val mealDao: MealDao
) {
    // --- Main APIs used by the new MealListViewModel ---
    fun getMealsSortedByDate(): Flow<List<Meal>> = mealDao.getAllMeals()

    //add Meal
    suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(meal)
    }

    // --- Existing APIs already in the project ---
    fun getMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    // Get all meals sorted by date (alias of getMealsSortedByDate for compatibility)
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    // Get all meals sorted by rating
    fun getAllMealsByRating(): Flow<List<Meal>> = mealDao.getAllMealsByRating()

    // Get all meals sorted by type
    fun getAllMealsByType(): Flow<List<Meal>> = mealDao.getAllMealsByType()

    // Get all meals sorted by calories
    fun getAllMealsByCalories(): Flow<List<Meal>> = mealDao.getAllMealsByCalories()

    // Search meals
    fun searchMeals(query: String): Flow<List<Meal>> {
        return mealDao.searchMeals(query)
    }

    // Delete a meal
    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal)
    }

    // Delete all meals
    suspend fun deleteAllMeals() {
        mealDao.deleteAllMeals()
    }
    suspend fun updateMeal(meal: Meal) {
        mealDao.updateMeal(meal)
    }

    // --- NEW: Daily nutrition totals (only today's meals) ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyNutritionTotals(): Flow<NutritionTotals> {
        val today = LocalDate.now()
        return mealDao.getAllMeals().map { meals ->
            val todaysMeals = meals.filter { it.date.toLocalDate() == today }
            NutritionTotals(
                calories = todaysMeals.sumOf { it.calories },
                protein  = todaysMeals.sumOf { it.protein },
                carbs    = todaysMeals.sumOf { it.carbs },
                fat      = todaysMeals.sumOf { it.fat }
            )
        }
    }
}
