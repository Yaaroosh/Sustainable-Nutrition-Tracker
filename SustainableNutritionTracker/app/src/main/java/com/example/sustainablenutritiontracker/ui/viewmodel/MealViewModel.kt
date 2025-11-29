package com.example.sustainablenutritiontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    // StateFlow for meals list
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    // Current sort type
    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    init {
        loadMeals()
    }

    // Load meals based on current sort type
    fun loadMeals(sortType: SortType = _sortType.value) {
        viewModelScope.launch {
            _sortType.value = sortType
            when (sortType) {
                SortType.DATE -> repository.getAllMeals()
                SortType.RATING -> repository.getAllMealsByRating()
                SortType.TYPE -> repository.getAllMealsByType()
                SortType.CALORIES -> repository.getAllMealsByCalories()
            }.collect { mealList ->
                _meals.value = mealList
            }
        }
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
