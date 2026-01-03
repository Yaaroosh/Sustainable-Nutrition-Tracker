package com.example.sustainablenutritiontracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sustainablenutritiontracker.data.repository.MealRepository
import com.example.sustainablenutritiontracker.data.repository.TodayMealRepository
import com.example.sustainablenutritiontracker.data.repository.SustainabilityRepository

class TodayViewModelFactory(
    private val todayRepo: TodayMealRepository,
    private val mealRepo: MealRepository,
    private val sustainabilityRepo: SustainabilityRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodayViewModel(todayRepo, mealRepo, sustainabilityRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
