package com.example.sustainablenutritiontracker.data.repository

import com.example.sustainablenutritiontracker.data.database.MealDao
import com.example.sustainablenutritiontracker.data.model.Meal
import kotlinx.coroutines.flow.Flow

class MealRepository(private val mealDao: MealDao) {

    // Get all meals sorted by date
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    // Get all meals sorted by rating
    fun getAllMealsByRating(): Flow<List<Meal>> = mealDao.getAllMealsByRating()

    // Get all meals sorted by type
    fun getAllMealsByType(): Flow<List<Meal>> = mealDao.getAllMealsByTyp()

    // Get all meals sorted by calories
    fun getAllMealsByCalories(): Flow<List<Meal>> = mealDao.getAllMealsByCalories()

    // Insert a new meal
    suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(meal)
    }

    // Delete a meal
    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal)
    }

    // Delete all meals
    suspend fun deleteAllMeals() {
        mealDao.deleteAllMeals()
    }
}
