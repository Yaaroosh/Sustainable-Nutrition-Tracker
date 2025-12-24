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
import com.example.sustainablenutritiontracker.ui.meal.TodayScreen
import com.example.sustainablenutritiontracker.ui.today.TodayViewModel
import com.example.sustainablenutritiontracker.ui.today.TodayViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModelFactory

// ---------- ROUTES ----------
const val HOME_ROUTE = "home"
const val LIST_ROUTE = "list"
const val ADD_MEAL_ROUTE = "addMeal"
const val EDIT_MEAL_ROUTE = "editMeal"
const val SET_GOALS_ROUTE = "setGoals"
const val TODAY_ROUTE = "today"

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = DatabaseProvider.provideRepository(context)
    val todayrepo = DatabaseProvider.provideTodayMealRepository(context)

    // ---------- VIEWMODELS ----------
    val mealListVM: MealListViewModel = viewModel(
        factory = MealListViewModelFactory(repository)
    )

    val mealVM: MealViewModel = viewModel(
        factory = MealViewModelFactory(repository)
    )

    val dailyGoalVM: DailyGoalViewModel = viewModel(
        factory = DailyGoalViewModelFactory(repository)
    )

    val todayVM: TodayViewModel = viewModel(
        factory = TodayViewModelFactory(todayrepo)
    )

    // ---------- FIRST TIME SETUP CHECK ----------
    val dailyGoals by dailyGoalVM.dailyGoals.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(dailyGoals) {
        if (!hasNavigated && dailyGoals == DailyGoals()) {
            navController.navigate(SET_GOALS_ROUTE) {
                popUpTo(HOME_ROUTE) { inclusive = true }
            }
            hasNavigated = true
        }
    }

    // ---------- NAV HOST ----------
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {

        composable(HOME_ROUTE) {
            HomeScreen(
                dailyGoalViewModel = dailyGoalVM,
                mealViewModel = mealVM,
                onNavigateToList = { navController.navigate(LIST_ROUTE) },
                onNavigateToAdd = { navController.navigate(ADD_MEAL_ROUTE) },
                onNavigateToSetGoals = { navController.navigate(SET_GOALS_ROUTE) },
                onNavigateToToday = { navController.navigate(TODAY_ROUTE) }
            )
        }

        composable(SET_GOALS_ROUTE) {
            SetDailyGoalScreen(
                viewModel = dailyGoalVM,
                onNavigateBack = {
                    navController.navigate(HOME_ROUTE) {
                        popUpTo(SET_GOALS_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(LIST_ROUTE) {
            MealListScreen(
                viewModel = mealListVM,
                onNavigateToAdd = { navController.navigate(ADD_MEAL_ROUTE) },
                onEditMeal = { mealId ->
                    navController.navigate("$EDIT_MEAL_ROUTE/$mealId")
                }
            )
        }

        composable(ADD_MEAL_ROUTE) {
            AddMealScreen(
                viewModel = mealVM,
                onSave = { navController.popBackStack() }
            )
        }

        composable("$EDIT_MEAL_ROUTE/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments
                ?.getString("mealId")
                ?.toLong()
                ?: return@composable

            EditMealScreen(
                mealId = mealId,
                viewModel = mealListVM,
                onSave = { navController.popBackStack() }
            )
        }

        composable(TODAY_ROUTE) {
            TodayScreen(
                viewModel = todayVM,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
