package com.example.sustainablenutritiontracker.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.NutritionTotals
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.ui.components.FilterType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val _selectedDay = MutableStateFlow(LocalDate.now())
    val selectedDay: StateFlow<LocalDate> = _selectedDay.asStateFlow()

    val nutritionTotals: StateFlow<NutritionTotals> =
        combine(repository.getMeals(), _selectedDay) { meals, day ->
            val todaysMeals = meals.filter { it.date.toLocalDate() == day }
            NutritionTotals(
                calories = todaysMeals.sumOf { it.calories },
                protein = todaysMeals.sumOf { it.protein },
                carbs = todaysMeals.sumOf { it.carbs },
                fat = todaysMeals.sumOf { it.fat }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NutritionTotals(0, 0, 0, 0)
        )

    init {
        observeMeals()
    }

    private fun observeMeals() {
        viewModelScope.launch {
            combine(
                repository.getMeals(),
                _selectedDay,
                _sortType,
                _searchQuery,
                _filterType
            ) { meals, day, sort, query, filter ->

                // 0) DAY FILTER
                val dayMeals = meals.filter { it.date.toLocalDate() == day }

                // 1) SORTING (unabhängig von FilterType!)
                val sorted = when (sort) {
                    SortType.DATE -> dayMeals.sortedByDescending { it.date }
                    SortType.RATING -> dayMeals.sortedByDescending { it.rating }
                    SortType.TYPE -> dayMeals.sortedBy { it.mealType }
                    SortType.CALORIES -> dayMeals.sortedByDescending { it.calories }
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

                // 3) FILTER (exhaustiv, inkl. neue Werte)
                searched.filter { meal ->
                    when (filter) {
                        FilterType.ALL -> true

                        // MealTypes
                        FilterType.BREAKFAST -> meal.mealType == "breakfast"
                        FilterType.LUNCH -> meal.mealType == "lunch"
                        FilterType.DINNER -> meal.mealType == "dinner"
                        FilterType.SNACK -> meal.mealType == "snack"

                        // Diet
                        FilterType.VEGETARIAN -> meal.vegetarian && !meal.containsMeat
                        FilterType.VEGAN -> meal.isVegan
                        FilterType.MEAT -> meal.containsMeat

                        // Existing
                        FilterType.LOW_CALORIES -> meal.calories < 500
                        FilterType.HIGH_PROTEIN -> meal.protein >= 20

                        // Sort-only FilterTypes (für MealListViewModel gedacht)
                        FilterType.BEST_RATING,
                        FilterType.WORST_RATING -> true
                    }
                }
            }.collect { finalList ->
                _meals.value = finalList
            }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateFilter(newFilter: FilterType) {
        _filterType.value = newFilter
    }

    fun loadMeals(sort: SortType = _sortType.value) {
        _sortType.value = sort
    }

    fun setSelectedDay(day: LocalDate) {
        _selectedDay.value = day
    }

    fun previousDay() {
        _selectedDay.value = _selectedDay.value.minusDays(1)
    }

    fun nextDay() {
        _selectedDay.value = _selectedDay.value.plusDays(1)
    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            repository.insertMeal(meal.copy(date = _selectedDay.value.toEpochMillis()))
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch { repository.deleteMeal(meal) }
    }

    fun deleteAllMeals() {
        viewModelScope.launch { repository.deleteAllMeals() }
    }

    fun editMeal(meal: Meal) {
        viewModelScope.launch { repository.updateMeal(meal) }
    }

    enum class SortType {
        DATE, RATING, TYPE, CALORIES
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
