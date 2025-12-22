package com.example.sustainablenutritiontracker.data.database

import androidx.room.*
import com.example.sustainablenutritiontracker.data.model.DailyGoals
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalsDao {

    @Query("SELECT * FROM daily_goals WHERE id = 1")
    fun getDailyGoals(): Flow<DailyGoals?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(goals: DailyGoals)

    @Query("SELECT * FROM daily_goals WHERE id = 1")
    suspend fun getDailyGoalsOnce(): DailyGoals?
}
