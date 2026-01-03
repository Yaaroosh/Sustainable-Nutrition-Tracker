package com.example.sustainablenutritiontracker.ui.components

enum class FilterType {
    ALL,

    // Meal types
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,

    // Diet filters
    VEGETARIAN,
    VEGAN,
    MEAT,

    // Existing filters (bleiben, damit MealViewModel nicht bricht)
    LOW_CALORIES,
    HIGH_PROTEIN,

    // Rating sorting (für MealListViewModel)
    BEST_RATING,
    WORST_RATING
}
