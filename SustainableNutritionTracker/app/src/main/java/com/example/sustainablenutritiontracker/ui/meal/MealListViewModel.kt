package com.example.sustainablenutritiontracker.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.ui.components.FilterType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MealListViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    /**
     * Wir nutzen nur repository.getMeals() (einfach & robust).
     * Sortierung/Filterung passieren im ViewModel.
     */
    val meals: StateFlow<List<Meal>> =
        combine(
            repository.getMeals(),
            _searchQuery,
            _filterType
        ) { allMeals, query, filter ->

            val q = query.trim()

            // 1) Sortierung (nur für BEST/WORST Rating)
            val sorted = when (filter) {
                FilterType.BEST_RATING -> allMeals.sortedWith(
                    compareByDescending<Meal> { it.rating }.thenByDescending { it.date }
                )
                FilterType.WORST_RATING -> allMeals.sortedWith(
                    compareBy<Meal> { it.rating }.thenByDescending { it.date }
                )
                else -> allMeals.sortedByDescending { it.date }
            }

            // 2) Search
            val searched = if (q.isBlank()) {
                sorted
            } else {
                sorted.filter {
                    it.title.contains(q, ignoreCase = true) ||
                            it.mealType.contains(q, ignoreCase = true)
                }
            }

            // 3) Filter (MealType + Diet + Existing)
            searched.filter { meal ->
                when (filter) {
                    FilterType.ALL,
                    FilterType.BEST_RATING,
                    FilterType.WORST_RATING -> true

                    // MealTypes
                    FilterType.BREAKFAST -> meal.mealType == "breakfast"
                    FilterType.LUNCH -> meal.mealType == "lunch"
                    FilterType.DINNER -> meal.mealType == "dinner"
                    FilterType.SNACK -> meal.mealType == "snack"

                    // Diet
                    FilterType.VEGAN -> meal.isVegan
                    FilterType.VEGETARIAN -> meal.vegetarian && !meal.containsMeat
                    FilterType.MEAT -> meal.containsMeat

                    // Existing filters
                    FilterType.LOW_CALORIES -> meal.calories < 500
                    FilterType.HIGH_PROTEIN -> meal.protein >= 20
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun updateFilter(filter: FilterType) {
        _filterType.value = filter
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch { repository.deleteMeal(meal) }
    }

    fun editMeal(meal: Meal) {
        viewModelScope.launch { repository.updateMeal(meal) }
    }
}
