package com.example.sustainablenutritiontracker.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sustainablenutritiontracker.data.database.DatabaseProvider
import com.example.sustainablenutritiontracker.data.model.DailyGoals
import com.example.sustainablenutritiontracker.ui.home.HomeScreen
import com.example.sustainablenutritiontracker.ui.home.SetDailyGoalScreen
import com.example.sustainablenutritiontracker.ui.meal.AddMealScreen
import com.example.sustainablenutritiontracker.ui.meal.EditMealScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModel
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModelFactory

const val TODAY_LIST_ROUTE = "today_list"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val repository = DatabaseProvider.provideRepository(context)

    // ViewModels
    val mealListVM: MealListViewModel = viewModel(
        factory = MealListViewModelFactory(repository)
    )

    val mealVM: MealViewModel = viewModel(
        factory = MealViewModelFactory(repository)
    )

    val dailyGoalVM: DailyGoalViewModel = viewModel(
        factory = DailyGoalViewModelFactory(repository)
    )

    // First-Time Check: Wenn Default-Werte → zu SetGoals navigieren
    val dailyGoals by dailyGoalVM.dailyGoals.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(dailyGoals) {
        if (!hasNavigated && dailyGoals == DailyGoals()) {
            navController.navigate("setGoals") {
                popUpTo("home") { inclusive = true }
            }
            hasNavigated = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                dailyGoalViewModel = dailyGoalVM,
                mealViewModel = mealVM,
                onNavigateToList = { navController.navigate("list") },
                onNavigateToAdd = { navController.navigate("addMeal") },
                onNavigateToSetGoals = { navController.navigate("setGoals") }
            )
        }

        composable("setGoals") {
            SetDailyGoalScreen(
                viewModel = dailyGoalVM,
                onNavigateBack = {
                    navController.navigate("home") {
                        popUpTo("setGoals") { inclusive = true }
                    }
                }
            )
        }

        composable("list") {
            MealListScreen(
                viewModel = mealListVM,
                onNavigateToAdd = { navController.navigate("addMeal") },
                onEditMeal = { id ->
                    navController.navigate("editMeal/$id")
                }
            )
        }

        composable("addMeal") {
            AddMealScreen(
                viewModel = mealVM,
                onSave = { navController.popBackStack() },
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
