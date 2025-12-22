package com.example.sustainablenutritiontracker.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.NutritionTotals
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.sustainablenutritiontracker.data.model.toLocalDate
import com.example.sustainablenutritiontracker.ui.components.FilterType


@RequiresApi(Build.VERSION_CODES.O)
class MealViewModel(private val repository: MealRepository) : ViewModel() {

    // StateFlow for meals list
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()



    // search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    //filter state
    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    // Current sort type
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
        observeMeals()      // ADDED — replaces loadMeals() as primary flow collector
    }

    // ADDED — observe database changes + apply SORT + SEARCH + FILTER together
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeMeals() {
        viewModelScope.launch {
            combine(
                repository.getMeals(),   // DB flow
                _sortType,
                _searchQuery,
                _filterType
            ) { meals, sort, query, filter ->



                // 1) SORTING

                val todayMeals = meals.filter { it.date.toLocalDate() == java.time.LocalDate.now() }
                val sorted = when (sort) {
                    SortType.DATE -> todayMeals.sortedByDescending { it.date }
                    SortType.RATING -> todayMeals.sortedByDescending { it.rating }
                    SortType.TYPE -> todayMeals.sortedBy { it.mealType }
                    SortType.CALORIES -> todayMeals.sortedByDescending { it.calories }
                }

                // 2) SEARCH
                val searched = if (query.isBlank()) {
                    sorted
                } else {
                    sorted.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.mealType.contains(query, ignoreCase = true)
                    }
                }

                // FILTER
                val filtered = searched.filter { meal ->
                    when (filter) {
                        FilterType.ALL -> true
                        FilterType.VEGETARIAN -> !meal.containsMeat
                        FilterType.VEGAN -> meal.isVegan
                        FilterType.MEAT -> meal.containsMeat
                        FilterType.LOW_CALORIES -> meal.calories < 500
                        FilterType.HIGH_PROTEIN -> meal.protein >= 20
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

    // ADDED — called from Filter UI
    fun updateFilter(newFilter: FilterType) {
        _filterType.value = newFilter
    }

    // Load meals based on current sort type
    fun loadMeals(sort: SortType = _sortType.value) {
        _sortType.value = sort
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

    fun editMeal(meal: Meal) {
        viewModelScope.launch {
            repository.updateMeal(meal)
        }
    }

    // Enum for sort types
    enum class SortType {
        DATE, RATING, TYPE, CALORIES
    }
}
