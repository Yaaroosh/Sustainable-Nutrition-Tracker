package com.example.sustainablenutritiontracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayMealDao {

    @Query("SELECT * FROM today_meals WHERE date = :today")
    fun getTodayMeals(today: String): Flow<List<TodayMealEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodayMeal(meal: TodayMealEntity)

    @Query("DELETE FROM today_meals WHERE id = :id")
    suspend fun deleteTodayMeal(id: Long)

    // daily reset
    @Query("DELETE FROM today_meals WHERE date < :today")
    suspend fun deleteOldMeals(today: String)
}