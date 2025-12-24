package com.example.sustainablenutritiontracker.ui.meal


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.sustainablenutritiontracker.ui.components.FilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    viewModel: MealListViewModel,
    onEditMeal: (Long) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val meals by viewModel.meals.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.filterType.collectAsState()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(title =
                { Text("Meals") },


                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = { Text("Search meals") }
                )

                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == FilterType.ALL,
                        onClick = { viewModel.updateFilter(FilterType.ALL) },
                        label = { Text("ALL") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterType.VEGETARIAN,
                        onClick = { viewModel.updateFilter(FilterType.VEGETARIAN) },
                        label = { Text("VEGETARIAN") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterType.VEGAN,
                        onClick = { viewModel.updateFilter(FilterType.VEGAN) },
                        label = { Text("VEGAN") }
                    )
                    FilterChip(
                        selected = selectedFilter == FilterType.MEAT,
                        onClick = { viewModel.updateFilter(FilterType.MEAT) },
                        label = { Text("MEAT") }
                    )


                }

            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {
            items(meals, key = { it.id }) { meal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(meal.title, fontWeight = FontWeight.Bold)
                            Text("${meal.calories} kcal • ${meal.mealType}")
                        }

                        //  EDIT
                        IconButton(onClick = { onEditMeal(meal.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        // DELETE
                        IconButton(onClick = { viewModel.deleteMeal(meal) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}
