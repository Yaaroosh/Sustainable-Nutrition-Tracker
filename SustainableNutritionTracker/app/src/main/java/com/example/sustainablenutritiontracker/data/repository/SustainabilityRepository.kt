package com.example.sustainablenutritiontracker.data.repository

import com.example.sustainablenutritiontracker.data.database.SustainableDayDao
import com.example.sustainablenutritiontracker.data.model.SustainableDayEntity
import kotlinx.coroutines.flow.Flow

class SustainabilityRepository(
    private val dao: SustainableDayDao
) {
    suspend fun upsert(day: SustainableDayEntity) = dao.upsert(day)

    fun lastDays(today: String, limit: Int): Flow<List<SustainableDayEntity>> =
        dao.getLastDays(today, limit)

    suspend fun clearAll() = dao.clearAll()
}
