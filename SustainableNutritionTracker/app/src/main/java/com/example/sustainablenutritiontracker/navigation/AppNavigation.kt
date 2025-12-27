package com.example.sustainablenutritiontracker.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sustainablenutritiontracker.data.database.DatabaseProvider
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.ui.home.HomeScreen
import com.example.sustainablenutritiontracker.ui.home.SetDailyGoalScreen
import com.example.sustainablenutritiontracker.ui.meal.AddMealScreen
import com.example.sustainablenutritiontracker.ui.meal.EditMealScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModel
import com.example.sustainablenutritiontracker.ui.meal.MealListViewModelFactory
import com.example.sustainablenutritiontracker.ui.today.TodayScreen
import com.example.sustainablenutritiontracker.ui.today.TodayViewModel
import com.example.sustainablenutritiontracker.ui.today.TodayViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModelFactory
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModelFactory

private const val HOME = "home"
private const val LIST = "list"
private const val ADD = "addMeal"
private const val EDIT = "editMeal"
private const val TODAY = "today"
private const val PICK = "pickMeal"
private const val SET_GOALS = "setGoals"

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val context = LocalContext.current

    val mealRepo = DatabaseProvider.provideRepository(context)
    val todayRepo = DatabaseProvider.provideTodayRepository(context)

    // Meal templates / browsing list
    val mealListVM: MealListViewModel =
        viewModel(factory = MealListViewModelFactory(mealRepo))

    // Template add/edit (still used by AddMealScreen)
    val mealVM: MealViewModel =
        viewModel(factory = MealViewModelFactory(mealRepo))

    // Goals
    val dailyGoalVM: DailyGoalViewModel =
        viewModel(factory = DailyGoalViewModelFactory(mealRepo))

    // Consumed meals (today_meals) + per-day navigation
    val todayVM: TodayViewModel =
        viewModel(factory = TodayViewModelFactory(todayRepo, mealRepo))

    // --- gram dialog state (picker flow) ---
    var pendingMeal by remember { mutableStateOf<Meal?>(null) }
    var pendingType by remember { mutableStateOf("breakfast") }
    var gramsText by remember { mutableStateOf("100") }
    var showGramDialog by remember { mutableStateOf(false) }

    if (showGramDialog && pendingMeal != null) {
        AlertDialog(
            onDismissRequest = { showGramDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val grams = gramsText.toIntOrNull()?.coerceAtLeast(1) ?: 100
                    todayVM.addMealFromTemplate(
                        meal = pendingMeal!!,
                        grams = grams,
                        mealType = pendingType
                    )
                    showGramDialog = false
                    nav.popBackStack(TODAY, inclusive = false)
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showGramDialog = false }) { Text("Cancel") }
            },
            title = { Text("How many grams?") },
            text = {
                OutlinedTextField(
                    value = gramsText,
                    onValueChange = { gramsText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Grams") },
                    singleLine = true
                )
            }
        )
    }

    NavHost(navController = nav, startDestination = HOME) {

        composable(HOME) {
            // ✅ Home now shows ALWAYS-TODAY totals from today_meals
            HomeScreen(
                dailyGoalViewModel = dailyGoalVM,
                todayViewModel = todayVM,
                onNavigateToList = { nav.navigate(LIST) },
                onNavigateToAdd = { nav.navigate(ADD) },
                onNavigateToSetGoals = { nav.navigate(SET_GOALS) },
                onNavigateToToday = { nav.navigate(TODAY) }
            )
        }

        composable(SET_GOALS) {
            SetDailyGoalScreen(
                viewModel = dailyGoalVM,
                onNavigateBack = { nav.popBackStack() }
            )
        }

        composable(LIST) {
            MealListScreen(
                viewModel = mealListVM,
                onNavigateToAdd = { nav.navigate(ADD) },
                onBack = { nav.popBackStack() },
                onEditMeal = { mealId: Long -> nav.navigate("$EDIT/$mealId") }
            )
        }

        composable(ADD) {
            AddMealScreen(
                viewModel = mealVM,
                onSave = { nav.popBackStack() },
                onBack = { nav.popBackStack() }
            )
        }

        composable("$EDIT/{mealId}") { entry ->
            val id = entry.arguments?.getString("mealId")?.toLongOrNull() ?: return@composable
            EditMealScreen(
                mealId = id,
                viewModel = mealListVM,
                onSave = { nav.popBackStack() },
                onBack = { nav.popBackStack() }
            )
        }

        composable(TODAY) {
            TodayScreen(
                viewModel = todayVM,
                onBack = { nav.popBackStack() },
                onPickMeal = { type: String ->
                    nav.navigate("$PICK/$type")
                }
            )
        }

        composable("$PICK/{mealType}") { entry ->
            val type = entry.arguments?.getString("mealType") ?: "breakfast"
            MealListScreen(
                viewModel = mealListVM,
                onNavigateToAdd = { nav.navigate(ADD) },
                onBack = { nav.popBackStack() },
                pickerMode = true,
                onPickMeal = { picked: Meal ->
                    pendingMeal = picked
                    pendingType = type
                    gramsText = "100"
                    showGramDialog = true
                }
            )
        }
    }
}
