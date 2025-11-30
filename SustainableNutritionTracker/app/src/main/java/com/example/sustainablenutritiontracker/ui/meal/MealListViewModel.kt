package com.example.sustainablenutritiontracker.ui.meal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.sustainablenutritiontracker.data.database.AppDatabase
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MealListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MealRepository

    val meals: StateFlow<List<Meal>>

    init {
        val db = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "sustainable_nutrition_db"
        ).build()

        repository = MealRepository(db.mealDao())

        meals = repository
            .getMealsSortedByDate()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
        }
    }

    fun deleteAllMeals() {
        viewModelScope.launch {
            repository.deleteAllMeals()
        }
    }
}
