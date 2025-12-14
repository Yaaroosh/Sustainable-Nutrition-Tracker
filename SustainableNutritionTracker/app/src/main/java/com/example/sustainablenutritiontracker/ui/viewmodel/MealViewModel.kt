package com.example.sustainablenutritiontracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.NutritionTotals
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    // --- Meals list state ---
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    // --- Search query state ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Sort type state ---
    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    // --- Daily nutrition totals (only today's meals) ---
    val nutritionTotals: StateFlow<NutritionTotals> =
        repository.getDailyNutritionTotals()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NutritionTotals(0, 0, 0, 0)
            )

    init {
        observeMeals()  // Start observing meals
    }

    // --- Observe database changes, apply sort + search ---
    private fun observeMeals() {
        viewModelScope.launch {
            combine(
                repository.getMeals(),  // DB flow
                _sortType,
                _searchQuery
            ) { meals, sort, query ->

                // --- Filter only today's meals ---
                val todayMeals = meals.filter { it.date.toLocalDate() == java.time.LocalDate.now() }

                // --- Sorting ---
                val sorted = when (sort) {
                    SortType.DATE -> todayMeals.sortedByDescending { it.date }
                    SortType.RATING -> todayMeals.sortedByDescending { it.rating }
                    SortType.TYPE -> todayMeals.sortedBy { it.mealType }
                    SortType.CALORIES -> todayMeals.sortedByDescending { it.calories }
                }

                // --- Search filter ---
                if (query.isBlank()) sorted
                else sorted.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.mealType.contains(query, ignoreCase = true)
                }

            }.collect { finalList ->
                _meals.value = finalList
            }
        }
    }

    // --- Update search query from UI ---
    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // --- Change sort type ---
    fun loadMeals(sort: SortType = _sortType.value) {
        _sortType.value = sort
        // DB collection is handled in observeMeals
    }

    // --- Add a new meal ---
    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            repository.insertMeal(meal)
        }
    }

    // --- Delete a meal ---
    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    // --- Delete all meals ---
    fun deleteAllMeals() {
        viewModelScope.launch {
            repository.deleteAllMeals()
        }
    }

    // --- Sort type enum ---
    enum class SortType {
        DATE, RATING, TYPE, CALORIES
    }
}
