package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayTotals
import com.example.sustainablenutritiontracker.ui.viewmodel.CO2PopupData
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

    // NEU: CO2 Tracking
    private val _showCO2Popup = MutableStateFlow<CO2PopupData?>(null)
    val showCO2Popup: StateFlow<CO2PopupData?> = _showCO2Popup.asStateFlow()

    private val _totalCO2Saved = MutableStateFlow(0.0)
    val totalCO2Saved: StateFlow<Double> = _totalCO2Saved.asStateFlow()

    val todayMeals: StateFlow<List<TodayMealEntity>> =
        date.flatMapLatest { d -> todayRepo.mealsForDate(d.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totals: StateFlow<TodayTotals> =
        date.flatMapLatest { d -> todayRepo.totals(d.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayTotals())

    private val todayKey: String get() = LocalDate.now().toString()

    val todayMealsNow: StateFlow<List<TodayMealEntity>> =
        todayRepo.mealsForDate(todayKey)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalsNow: StateFlow<TodayTotals> =
        todayRepo.totals(todayKey)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayTotals())

    init {
        observeTodaysCO2()
    }

    private fun observeTodaysCO2() {
        viewModelScope.launch {
            todayMealsNow.collect { mealsList ->
                calculateTodaysCO2(mealsList)
            }
        }
    }

    private fun calculateTodaysCO2(mealsList: List<TodayMealEntity>) {
        var totalCO2 = 0.0

        mealsList.forEach { entity ->
            val co2Impact = when {
                entity.isVegan -> 0.7
                entity.vegetarian -> 0.85
                else -> 1.4
            }

            // NEU: Bei Meat zählt negativ, bei Vegan/Vegetarian positiv
            if (entity.containsMeat || (!entity.isVegan && !entity.vegetarian)) {
                // Meat: subtrahiere absolute CO2 Menge
                totalCO2 -= co2Impact
            } else {
                // Vegan/Vegetarian: addiere gesparte CO2 Menge
                val regularBaseline = 1.4
                totalCO2 += (regularBaseline - co2Impact)
            }
        }

        _totalCO2Saved.value = totalCO2
    }

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

        viewModelScope.launch {
            todayRepo.add(entity)

            // GEÄNDERT: CO2 popup logic
            val co2Impact = meal.getCO2Impact()
            val regularBaseline = 1.4

            val popupData = if (meal.containsMeat || (!meal.isVegan && !meal.vegetarian)) {
                // Bei Meat: zeige absolute CO2 Menge als "produced"
                CO2PopupData(
                    amount = co2Impact,
                    isSaved = false,
                    dietType = meal.getDietTypeLabel()
                )
            } else {
                // Bei Vegan/Vegetarian: zeige Differenz als "saved"
                val difference = regularBaseline - co2Impact
                CO2PopupData(
                    amount = difference,
                    isSaved = true,
                    dietType = meal.getDietTypeLabel()
                )
            }

            _showCO2Popup.value = popupData

            // Auto-dismiss nach 5 Sekunden
            kotlinx.coroutines.delay(3000L)
            _showCO2Popup.value = null
        }
    }

    fun dismissCO2Popup() {
        _showCO2Popup.value = null
    }
}
