package com.example.sustainablenutritiontracker.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity
import com.example.sustainablenutritiontracker.viewmodel.TodayViewModel

// --- Wrapper Composable ---
@Composable
fun TodayScreen(
    viewModel: TodayViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit,
    onAddMealClicked: ((Meal) -> Unit)? = null // optional Add
) {
    val todayMeals by viewModel.todayMeals.collectAsState()

    TodayListScreen(
        items = todayMeals.map { it.toMeal() },
        onBack = onBack,
        onDeleteClicked = { meal ->
            viewModel.deleteMeal(meal.id)
        }
    )

    // Optional: Minimal Add Button (hier kannst du z. B. die MealList auswählen)
    // Beispiel: Button am unteren Rand
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (onAddMealClicked != null) {
            FloatingActionButton(
                onClick = { /* hier Meal auswählen und adden */ },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }
    }
}

// --- Mapper Funktion ---
fun TodayMealEntity.toMeal(): Meal {
    return Meal(
        id = this.mealId,
        title = this.title ?: "Unknown",
        calories = this.calories ?: 0,
        protein = this.protein ?: 0,
        carbs = this.carbs ?: 0,
        fat = this.fat ?: 0,
        mealType = this.mealType ?: "Unknown"
    )
}

// --- TodayListScreen (bestehend, unverändert) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayListScreen(
    items: List<Meal>,
    onBack: () -> Unit,
    onDeleteClicked: (Meal) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Today's List") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No items in today's list yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { meal ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = meal.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${meal.calories} kcal • ${meal.mealType}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            IconButton(onClick = { onDeleteClicked(meal) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
