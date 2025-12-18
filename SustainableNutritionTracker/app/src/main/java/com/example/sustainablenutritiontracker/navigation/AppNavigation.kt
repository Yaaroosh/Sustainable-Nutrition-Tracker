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
import com.example.sustainablenutritiontracker.ui.meal.EditMealScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModel

import com.example.sustainablenutritiontracker.ui.meal.MealListViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.sustainablenutritiontracker.ui.today.TodayListScreen

const val TODAY_LIST_ROUTE = "today_list"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val repository = DatabaseProvider.provideRepository(context)

    // ViewModel für LISTE + FILTER
    val mealListVM: MealListViewModel = viewModel(
        factory = MealListViewModelFactory(repository)
    )

    //  ViewModel für ADD / WRITE
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
                onNavigateToAdd = { navController.navigate("addMeal") },
                onEditMeal = { id ->
                    navController.navigate("editMeal/$id")
                },
                onNavigateToTodayList = { navController.navigate(TODAY_LIST_ROUTE) }
            )
        }

        composable("addMeal") {
            AddMealScreen(
                viewModel = mealVM,
                onSave = { navController.popBackStack() },

            )
        }

        composable(TODAY_LIST_ROUTE) {
            val meals by mealListVM.meals.collectAsState()

            TodayListScreen(
                items = meals, // TEMP: uses existing meals list (NOT issue #44 storage)
                onBack = { navController.popBackStack() },
                onDeleteClicked = { meal -> mealListVM.deleteMeal(meal) }
            )
        }


        composable("editMeal/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId")!!.toInt()
            EditMealScreen(
                mealId = mealId,
                viewModel = mealListVM,
                onSave = { navController.popBackStack() }
            )
        }
    }
}

