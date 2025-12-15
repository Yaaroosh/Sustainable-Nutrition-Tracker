package com.example.sustainablenutritiontracker.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.ui.viewmodel.FilterType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MealListViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType

    val meals: StateFlow<List<Meal>> =
        combine(
            repository.getMeals(),
            _searchQuery,
            _filterType
        ) { meals, query, filter ->

            meals
                .filter {
                    query.isBlank() ||
                            it.title.contains(query, true) ||
                            it.mealType.contains(query, true)
                }
                .filter { meal ->
                    when (filter) {
                        FilterType.ALL -> true
                        FilterType.VEGETARIAN -> !meal.containsMeat
                        FilterType.VEGAN -> meal.isVegan
                        FilterType.MEAT -> meal.containsMeat
                        FilterType.LOW_CALORIES -> meal.calories < 500
                        FilterType.HIGH_PROTEIN -> meal.protein >= 20
                    }
                }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: FilterType) {
        _filterType.value = filter
    }

    fun deleteMeal(meal: Meal) = viewModelScope.launch {
        repository.deleteMeal(meal)
    }

    fun editMeal(meal: Meal) {
        viewModelScope.launch {
            repository.updateMeal(meal)
        }
    }
}
