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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class MealListViewModel(
    private val repository: MealRepository
) : ViewModel() {
    // ADDED (assignment #19): query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // ADDED switch the meals flow based on query
    val meals: StateFlow<List<Meal>> =
        _searchQuery
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    repository.getMealsSortedByDate()
                } else {
                    repository.searchMeals(query) // uses DAO query
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // ADDED called from UI
    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }


    fun deleteMeal(meal: Meal) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMeal(meal)
    }

    fun deleteAllMeals() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllMeals()
    }
}
