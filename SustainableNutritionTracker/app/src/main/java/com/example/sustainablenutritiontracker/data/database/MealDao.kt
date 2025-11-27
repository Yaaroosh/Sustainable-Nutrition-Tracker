package com.example.sustainablenutritiontracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sustainablenutritiontracker.data.model.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert
    suspend fun insertMeal(meal: Meal){

    }

    @Delete
    suspend fun deleteMeal(meal: Meal)


    //Sort by date
    @Query("SELECT * FROM meals ORDER BY date DESC")
    fun getAllMeals(): Flow<List<Meal>>

    //Sort by rating/date
    @Query("SELECT * FROM meals ORDER BY rating DESC,date DESC")
    fun getAllMealsByRating(): Flow<List<Meal>>

    //Sort by meal typ and date
    @Query("SELECT * FROM meals ORDER BY mealType ASC,date DESC")
    fun getAllMealsByTyp(): Flow<List<Meal>>

//Sort by calories
    @Query("SELECT * FROM meals ORDER BY calories ASC")
    fun getAllMealsByCalories(): Flow<List<Meal>>







//delete all meals
    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()


}
