package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository

class TodayViewModelFactory(
    private val todayRepo: TodayMealRepository,
    private val mealRepo: MealRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayViewModel(todayRepo, mealRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
