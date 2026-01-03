package com.example.sustainablenutritiontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.DailyGoals
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DailyGoalViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val defaultGoals = DailyGoals()


    val dailyGoals: StateFlow<DailyGoals> =
        repository.getDailyGoals()
            .map { it ?: defaultGoals }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = defaultGoals
            )


    fun updateGoals(
        caloriesLimit: Int,
        carbsLimit: Int,
        fatLimit: Int,
        proteinLimit: Int
    ) {
        viewModelScope.launch {
            val newGoals = DailyGoals(
                id = 1,
                caloriesLimit = caloriesLimit.coerceAtLeast(0),
                carbsLimit = carbsLimit.coerceAtLeast(0),
                fatLimit = fatLimit.coerceAtLeast(0),
                proteinLimit = proteinLimit.coerceAtLeast(0),
                isInitialized = true
            )
            repository.updateDailyGoals(newGoals)
        }
    }
}
