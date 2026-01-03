package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayTotals
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

class TodayViewModel(
    private val todayRepo: TodayMealRepository,
    private val mealRepo: MealRepository
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    // ✅ Selected day (für TodayScreen – Tage wechseln)
    val todayMeals: StateFlow<List<TodayMealEntity>> =
        date.flatMapLatest { d -> todayRepo.mealsForDate(d.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totals: StateFlow<TodayTotals> =
        date.flatMapLatest { d -> todayRepo.totals(d.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayTotals())

    // ✅ Always "real today" (für HomeScreen – IMMER heute)
    private val todayKey: String get() = LocalDate.now().toString()

    val todayMealsNow: StateFlow<List<TodayMealEntity>> =
        todayRepo.mealsForDate(todayKey)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalsNow: StateFlow<TodayTotals> =
        todayRepo.totals(todayKey)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayTotals())

    fun previousDay() { _date.value = _date.value.minusDays(1) }
    fun nextDay() { _date.value = _date.value.plusDays(1) }
    fun setDate(newDate: LocalDate) { _date.value = newDate }

    fun deleteTodayMeal(id: Long) {
        viewModelScope.launch { todayRepo.delete(id) }
    }

    fun addMealFromTemplate(meal: Meal, grams: Int, mealType: String, date: LocalDate = _date.value) {
        val safeGrams = grams.coerceAtLeast(1)
        val factor = safeGrams / 100f

        val entity = TodayMealEntity(
            mealId = meal.id,
            title = meal.title,
            mealType = mealType,
            grams = safeGrams,
            calories = (meal.calories * factor).roundToInt(),
            carbs = (meal.carbs * factor).roundToInt(),
            fat = (meal.fat * factor).roundToInt(),
            protein = (meal.protein * factor).roundToInt(),
            isVegan = meal.isVegan,
            containsMeat = meal.containsMeat,
            vegetarian = meal.vegetarian,
            date = date.toString()
        )

        viewModelScope.launch { todayRepo.add(entity) }
    }
}
