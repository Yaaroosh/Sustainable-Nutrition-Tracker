package com.example.sustainablenutritiontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.DailyGoals
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DailyGoalViewModel(private val repository: MealRepository) : ViewModel() {

    private val _dailyGoals = MutableStateFlow(DailyGoals())
    val dailyGoals: StateFlow<DailyGoals> = _dailyGoals

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.getDailyGoals().collect { goals ->
                _dailyGoals.value = goals ?: DailyGoals()
            }
        }
    }

    fun updateGoals(caloriesLimit: Int, carbsLimit: Int, fatLimit: Int, proteinLimit: Int) {
        viewModelScope.launch {
            val newGoals = DailyGoals(
                id = 1,
                caloriesLimit = caloriesLimit,
                carbsLimit = carbsLimit,
                fatLimit = fatLimit,
                proteinLimit = proteinLimit
            )
            repository.updateDailyGoals(newGoals)
        }
    }
}
