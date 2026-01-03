package com.example.sustainablenutritiontracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodayMealDao {

    @Query("SELECT * FROM today_meals WHERE date = :date ORDER BY id DESC")
    fun getMealsForDate(date: String): Flow<List<TodayMealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodayMeal(entity: TodayMealEntity)

    @Query("DELETE FROM today_meals WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM today_meals WHERE date = :date")
    suspend fun deleteAllForDate(date: String)

    @Query("SELECT IFNULL(SUM(calories),0) FROM today_meals WHERE date = :date")
    fun totalCalories(date: String): Flow<Int>

    @Query("SELECT IFNULL(SUM(carbs),0) FROM today_meals WHERE date = :date")
    fun totalCarbs(date: String): Flow<Int>

    @Query("SELECT IFNULL(SUM(fat),0) FROM today_meals WHERE date = :date")
    fun totalFat(date: String): Flow<Int>

    @Query("SELECT IFNULL(SUM(protein),0) FROM today_meals WHERE date = :date")
    fun totalProtein(date: String): Flow<Int>
}
