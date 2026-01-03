package com.example.sustainablenutritiontracker.ui.meal


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.ui.components.FilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealListScreen(
    viewModel: MealListViewModel,
    onNavigateToAdd: () -> Unit,
    onBack: () -> Unit,
    onEditMeal: (Long) -> Unit = {},
    pickerMode: Boolean = false,
    onPickMeal: (Meal) -> Unit = {}
) {
    val meals by viewModel.meals.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.filterType.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = { Text(if (pickerMode) "Pick a Meal" else "Meals") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Search meals") },
                    singleLine = true
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.ALL,
                            onClick = { viewModel.updateFilter(FilterType.ALL) },
                            label = { Text("ALL") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.VEGETARIAN,
                            onClick = { viewModel.updateFilter(FilterType.VEGETARIAN) },
                            label = { Text("VEGETARIAN") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.VEGAN,
                            onClick = { viewModel.updateFilter(FilterType.VEGAN) },
                            label = { Text("VEGAN") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.MEAT,
                            onClick = { viewModel.updateFilter(FilterType.MEAT) },
                            label = { Text("MEAT") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.BREAKFAST,
                            onClick = { viewModel.updateFilter(FilterType.BREAKFAST) },
                            label = { Text("BREAKFAST") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.LUNCH,
                            onClick = { viewModel.updateFilter(FilterType.LUNCH) },
                            label = { Text("LUNCH") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.DINNER,
                            onClick = { viewModel.updateFilter(FilterType.DINNER) },
                            label = { Text("DINNER") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.SNACK,
                            onClick = { viewModel.updateFilter(FilterType.SNACK) },
                            label = { Text("SNACK") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.BEST_RATING,
                            onClick = { viewModel.updateFilter(FilterType.BEST_RATING) },
                            label = { Text("BEST") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == FilterType.WORST_RATING,
                            onClick = { viewModel.updateFilter(FilterType.WORST_RATING) },
                            label = { Text("WORST") }
                        )
                    }

                }
            }
        },
        floatingActionButton = {
            if (!pickerMode) {
                FloatingActionButton(onClick = onNavigateToAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        if (meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No meals yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (pickerMode) Modifier.clickable { onPickMeal(meal) } else Modifier
                            ),
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${meal.calories} kcal")

                                if (meal.rating > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "Rating: ${meal.rating}/5",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (!pickerMode) {
                                IconButton(onClick = { onEditMeal(meal.id) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { viewModel.deleteMeal(meal) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
