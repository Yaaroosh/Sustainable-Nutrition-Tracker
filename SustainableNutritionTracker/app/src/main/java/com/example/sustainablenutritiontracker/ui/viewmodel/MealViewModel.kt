package com.example.sustainablenutritiontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    // StateFlow for meals list
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    // 🔍 search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Current sort type
    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    init {
        observeMeals()      // ADDED — replaces loadMeals() as primary flow collector
    }

    // ADDED — observe database changes + apply SORT + SEARCH together
    private fun observeMeals() {
        viewModelScope.launch {
            combine(
                repository.getMeals(),   // DB flow
                _sortType,
                _searchQuery
            ) { meals, sort, query ->

                // 1) SORTING (from loadMeals)
                val sorted = when (sort) {
                    SortType.DATE -> meals.sortedByDescending { it.date }
                    SortType.RATING -> meals.sortedByDescending { it.rating }
                    SortType.TYPE -> meals.sortedBy { it.mealType }
                    SortType.CALORIES -> meals.sortedByDescending { it.calories }
                }

                // 2) SEARCH (NEW FEATURE)
                val filtered = if (query.isBlank()) {
                    sorted
                } else {
                    sorted.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.mealType.contains(query, ignoreCase = true)
                    }
                }

                filtered
            }.collect { finalList ->
                _meals.value = finalList
            }
        }
    }

    // ADDED — called from SearchBar UI
    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }


    // Load meals based on current sort type
    fun loadMeals(sort: SortType = _sortType.value) {
        _sortType.value = sort

        // collection of db MOVED to observeMeals
    }



    // Add a new meal
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            repository.insertMeal(meal)
        }
    }

    // Delete a meal
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    // Delete all meals
    fun deleteAllMeals() {
        viewModelScope.launch {
            repository.deleteAllMeals()
        }
    }

    // Enum for sort types
    enum class SortType {
        DATE, RATING, TYPE, CALORIES
    }


}
