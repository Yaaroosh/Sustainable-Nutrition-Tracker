package com.example.sustainablenutritiontracker.ui.meal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons              // ADDED
import androidx.compose.material.icons.filled.Star      // ADDED
import androidx.compose.material.icons.outlined.StarBorder // ADDED
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment                    // ADDED (for align)
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions // ADDED
import androidx.compose.ui.text.input.KeyboardType      // ADDED
import androidx.compose.ui.unit.dp
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    viewModel: MealViewModel,
    onSave: () -> Unit          // caller handles navigation
) {
    var title by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }      // kept for future use
    var fat by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
    var mealType by remember { mutableStateOf(mealTypes.first()) }
    var expanded by remember { mutableStateOf(false) }
    var showMacroError by remember { mutableStateOf(false) } // NEW: state to show/hide macro error dialog
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Meal") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Meal title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it.filter { ch -> ch.isDigit() } },
                label = { Text("Calories") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            // 🔹 Carbs input
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it.filter { ch -> ch.isDigit() } },
                label = { Text("Carbs (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Fat input
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it.filter { ch -> ch.isDigit() } },
                label = { Text("Fat (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Protein input
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it.filter { ch -> ch.isDigit() } },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ---- Meal Type Dropdown ----
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = mealType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Meal Type") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    mealTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                mealType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // ---- Rating ----
            Text(text = "Rating", style = MaterialTheme.typography.titleMedium)
            RatingBar(
                rating = rating,
                onRatingSelected = { rating = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ---- Save Button ----
            Button(
                onClick = {
                    val caloriesInt = calories.toIntOrNull() ?: 0
                    val carbsInt = carbs.toIntOrNull() ?: 0
                    val fatInt = fat.toIntOrNull() ?: 0
                    val proteinInt = protein.toIntOrNull() ?: 0

                    try {
                        val meal = Meal(
                            title = title,
                            calories = caloriesInt,
                            carbs = carbsInt,
                            fat = fatInt,
                            protein = proteinInt,
                            mealType = mealType,
                            rating = rating
                        )
                        Log.d(
                            "MealDebug",
                            "Creating Meal -> calories=$caloriesInt, carbs=$carbsInt, fat=$fatInt, protein=$proteinInt"
                        )
                        viewModel.addMeal(meal)
                        onSave()

                    } catch (e: IllegalArgumentException) {
                        showMacroError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End)
            ) {
                Text("Save meal")
            }

            // ---- Alert Dialog (macro error) ----
            if (showMacroError) {
                AlertDialog(
                    onDismissRequest = { showMacroError = false },
                    confirmButton = {
                        TextButton(onClick = { showMacroError = false }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Invalid nutrition values") },
                    text = {
                        Text(
                            "The macronutrients appear too low in relation to calories. " +
                                    "Please adjust the values."
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RatingBar(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    maxRating: Int = 5
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..maxRating) {
            IconButton(onClick = { onRatingSelected(i) }) {
                if (i <= rating) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star $i",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = "Star $i"
                    )
                }
            }
        }
    }
}
