package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import com.example.sustainablenutritiontracker.data.model.SustainableDayEntity
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayTotals
import com.example.sustainablenutritiontracker.ui.viewmodel.CO2PopupData
import com.example.sustainablenutritiontracker.data.repository.SustainabilityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


private const val SUSTAINABLE_THRESHOLD = 70
private const val STREAK_LOOKBACK_DAYS = 60 // how many past days we consider when computing streak

class TodayViewModel(
    private val todayRepo: TodayMealRepository,
    private val mealRepo: MealRepository,
    private val sustainabilityRepo: SustainabilityRepository
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

    private fun calculateCO2Impact(meals: List<TodayMealEntity>): Double {
        return meals.sumOf { entity ->
            val co2Impact = when {
                entity.isVegan -> 0.7
                entity.vegetarian -> 0.85
                else -> 1.4
            }
            if (entity.containsMeat || (!entity.isVegan && !entity.vegetarian)) {
                -co2Impact
            } else {
                1.4 - co2Impact
            }
        }
    }

    private fun calculateTodaysCO2(mealsList: List<TodayMealEntity>) {
        _totalCO2Saved.value = calculateCO2Impact(mealsList)
    }
    // TODO(issue-58): replace with real environment score
    private val _environmentScore = MutableStateFlow(0)
    val environmentScore: StateFlow<Int> = _environmentScore.asStateFlow()

    val last7DaysCO2: StateFlow<List<Double>> = combine(
        (0..6).map { offset ->
            val targetDate = LocalDate.now().minusDays(offset.toLong()).toString()
            todayRepo.mealsForDate(targetDate).map { meals ->
                calculateCO2Impact(meals)
            }
        }
    ) { dailyValues ->
        dailyValues.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val isTodaySustainable: StateFlow<Boolean> =
        environmentScore
            .map { it >= SUSTAINABLE_THRESHOLD }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    // Observe DB days and compute streak continuously
    private val streakDaysFlow: Flow<List<SustainableDayEntity>> =
        sustainabilityRepo.lastDays(today = todayKey, limit = STREAK_LOOKBACK_DAYS)

    init {
        // Whenever DB changes, recompute streak
        viewModelScope.launch {
            streakDaysFlow.collect { days ->
                _streak.value = computeConsecutiveStreak(days)
            }
        }

        // Whenever environment score changes, persist "today" and DB will update streak
        viewModelScope.launch {
            environmentScore.collect { score ->
                persistTodaySustainability(score)
            }
        }
    }

    private suspend fun persistTodaySustainability(score: Int) {
        val clamped = score.coerceIn(0, 100)
        val day = SustainableDayEntity(
            date = todayKey,
            environmentScore = clamped,
            isSustainable = clamped >= SUSTAINABLE_THRESHOLD
        )
        sustainabilityRepo.upsert(day)
    }

    private fun computeConsecutiveStreak(daysDesc: List<SustainableDayEntity>): Int {
        // daysDesc: newest -> oldest
        var count = 0
        var expectedDate = LocalDate.now()

        for (day in daysDesc) {
            val dayDate = runCatching { LocalDate.parse(day.date) }.getOrNull() ?: break

            // If missing a day in between -> break streak
            if (dayDate != expectedDate) break

            if (!day.isSustainable) break

            count += 1
            expectedDate = expectedDate.minusDays(1)
        }
        return count
    }

    /**
     * Call this from UI / debug to simulate the score for *today*.
     * This is how you can "connect" to score logic without implementing #58.
     */
    fun setEnvironmentScore(score: Int) {
        _environmentScore.value = score
    }

    // =========================================================
    // Option B testing helpers: simulate days in DB
    // =========================================================

    /**
     * Writes a simulated score for a specific date (yyyy-MM-dd)
     */
    fun debugSimulateDay(date: LocalDate, score: Int) {
        viewModelScope.launch {
            sustainabilityRepo.upsert(
                SustainableDayEntity(
                    date = date.toString(),
                    environmentScore = score.coerceIn(0, 100),
                    isSustainable = score >= SUSTAINABLE_THRESHOLD
                )
            )
        }
    }

    /**
     * Seeds a streak ending today: e.g. days = 3 => today, yesterday, day before are sustainable.
     */
    fun debugSeedStreak(days: Int, sustainableScore: Int = 80) {
        viewModelScope.launch {
            val score = sustainableScore.coerceIn(0, 100)
            for (i in 0 until days) {
                val d = LocalDate.now().minusDays(i.toLong())
                sustainabilityRepo.upsert(
                    SustainableDayEntity(
                        date = d.toString(),
                        environmentScore = score,
                        isSustainable = score >= SUSTAINABLE_THRESHOLD
                    )
                )
            }
        }
    }

    fun debugBreakYesterday(unsustainableScore: Int = 10) {
        debugSimulateDay(LocalDate.now().minusDays(1), unsustainableScore)
    }

    fun debugClearStreakData() {
        viewModelScope.launch {
            sustainabilityRepo.clearAll()
        }
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
