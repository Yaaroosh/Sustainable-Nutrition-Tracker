package com.example.sustainablenutritiontracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sustainablenutritiontracker.data.database.DatabaseProvider
import com.example.sustainablenutritiontracker.ui.home.HomeScreen
import com.example.sustainablenutritiontracker.ui.meal.AddMealScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModel

import com.example.sustainablenutritiontracker.ui.meal.MealListViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val repository = DatabaseProvider.provideRepository(context)

    val mealListVM: MealListViewModel = viewModel(
        factory = MealListViewModelFactory(repository)
    )

    val mealVM: MealViewModel = viewModel(
        factory = MealViewModelFactory(repository)
    )


    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                onNavigateToList = { navController.navigate("list") },
                onNavigateToAdd = { navController.navigate("addMeal") }
            )
        }

        composable("list") {
            MealListScreen(
                viewModel = mealListVM,
                onNavigateToAdd = { navController.navigate("addMeal") }
            )
        }

        composable("addMeal") {
            AddMealScreen(
                viewModel = mealVM,
                onSave = { navController.popBackStack() }
            )
        }
    }
}
