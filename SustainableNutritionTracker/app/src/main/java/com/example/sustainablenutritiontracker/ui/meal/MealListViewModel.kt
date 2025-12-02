package com.example.sustainablenutritiontracker.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealListViewModel(
    private val repository: MealRepository
) : ViewModel() {

    val meals: StateFlow<List<Meal>> =
        repository
            .getMealsSortedByDate()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun deleteMeal(meal: Meal) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMeal(meal)
    }

    fun deleteAllMeals() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllMeals()
    }
}
