package com.example.sustainablenutritiontracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sustainablenutritiontracker.ui.home.HomeScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
        composable("mealList") { MealListScreen() }
    }
}
