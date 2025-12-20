package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodayViewModel(
    private val repository: TodayMealRepository
) : ViewModel() {

    val todayMeals = repository
        .getTodayMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.dailyReset()
        }
    }

    fun addMeal(mealId: Long) {
        viewModelScope.launch {
            repository.addMeal(mealId)
        }
    }

    fun deleteMeal(todayMealId: Long) {
        viewModelScope.launch {
            repository.deleteMeal(todayMealId)
        }
    }
}
