package com.example.sustainablenutritiontracker.ui.meal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sustainablenutritiontracker.data.model.Meal
import androidx.compose.ui.text.input.ImeAction // ADDED
import androidx.compose.foundation.text.KeyboardOptions // ADDED
import androidx.compose.material.icons.filled.Search // ADDED


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    viewModel: MealListViewModel,
    onMealClick: (Meal) -> Unit = {},
    onNavigateToAdd: () -> Unit
) {
    val meals by viewModel.meals.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("Meals") }
                )

                // ADDED: search bar
                val query by viewModel.searchQuery.collectAsState()
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    singleLine = true,
                    placeholder = { Text("Search meals (title or type)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }
    ) { innerPadding ->
        if (meals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No meals yet. Add one to get started.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    MealListItem(
                        meal = meal,
                        onClick = { onMealClick(meal) },
                        onDeleteClicked = { viewModel.deleteMeal(meal) }
                    )
                }
            }
        }
    }
}

@Composable
fun MealListItem(
    meal: Meal,
    onDeleteClicked: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
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

            // ADDED: show rating if present (0–5)
            if (meal.rating > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Rating: ${meal.rating}/5",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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

