package com.example.sustainablenutritiontracker.ui.meal

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sustainablenutritiontracker.data.model.Meal


class MealListViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    viewModel: MealListViewModel = viewModel(
        factory = MealListViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val meals by viewModel.meals.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meals") }
            )
        }
    ) { innerPadding ->
        if (meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No meals yet. Add one to get started.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(meals, key = { it.id }) { meal ->
                    MealListItem(
                        meal = meal,
                        onDeleteClicked = { viewModel.deleteMeal(meal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MealListItem(
    meal: Meal,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${meal.calories} kcal • ${meal.mealType}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onDeleteClicked) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete meal"
                )
            }
        }
    }
}

