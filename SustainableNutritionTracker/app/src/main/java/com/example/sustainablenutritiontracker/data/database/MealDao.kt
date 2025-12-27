package com.example.sustainablenutritiontracker.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sustainablenutritiontracker.data.model.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Update
    suspend fun updateMeal(meal: Meal)


    @Delete
    suspend fun deleteMeal(meal: Meal)


    //Sort by date
    @Query("SELECT * FROM meals ORDER BY date DESC")
    fun getAllMeals(): Flow<List<Meal>>

    //Sort by rating/date
    @Query("SELECT * FROM meals ORDER BY rating DESC,date DESC")
    fun getAllMealsByRating(): Flow<List<Meal>>

    //Sort by meal typ and date
    @Query("""SELECT * FROM meals ORDER BY 
        CASE mealType
            WHEN 'breakfast' THEN 1
            WHEN 'lunch' THEN 2
            WHEN 'dinner' THEN 3
            WHEN 'snack' THEN 4
        END,
        date DESC""")
    fun getAllMealsByType(): Flow<List<Meal>>


//Sort by calories
    @Query("SELECT * FROM meals ORDER BY calories ASC, date DESC")
    fun getAllMealsByCalories(): Flow<List<Meal>>

    @Query("""
    SELECT * FROM meals
    WHERE title LIKE '%' || :query || '%'
       OR mealType LIKE '%' || :query || '%'
    ORDER BY date DESC
""")
    fun searchMeals(query: String): Flow<List<Meal>>

    @Query("SELECT * FROM meals ORDER BY rating ASC, date DESC")
    fun getAllMealsByWorstRating(): Flow<List<Meal>>



//delete all meals
    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()


}
