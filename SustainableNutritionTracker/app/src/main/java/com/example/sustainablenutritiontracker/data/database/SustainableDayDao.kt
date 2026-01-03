package com.example.sustainablenutritiontracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sustainablenutritiontracker.data.model.SustainableDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SustainableDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(day: SustainableDayEntity)

    // Get last N days (including today) ordered newest -> oldest
    @Query("""
        SELECT * FROM sustainable_days
        WHERE date <= :today
        ORDER BY date DESC
        LIMIT :limit
    """)
    fun getLastDays(today: String, limit: Int): Flow<List<SustainableDayEntity>>

    @Query("DELETE FROM sustainable_days")
    suspend fun clearAll()
}
