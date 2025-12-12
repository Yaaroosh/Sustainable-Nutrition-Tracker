package com.example.sustainablenutritiontracker.ui.meal

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sustainablenutritiontracker.data.model.Meal
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    viewModel: MealViewModel,
    onSave: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
    var mealType by remember { mutableStateOf(mealTypes.first()) }
    var expanded by remember { mutableStateOf(false) }

    // ---- Validation error states ----
    var titleError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }
    var carbsError by remember { mutableStateOf<String?>(null) }
    var fatError by remember { mutableStateOf<String?>(null) }
    var proteinError by remember { mutableStateOf<String?>(null) }
    var showMacroError by remember { mutableStateOf(false) }

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

            // ---- Title ----
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = { Text("Meal title") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null,
                supportingText = {
                    titleError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // ---- Calories ----
            OutlinedTextField(
                value = calories,
                onValueChange = {
                    calories = it.filter { ch -> ch.isDigit() }
                    caloriesError = null
                },
                label = { Text("Calories") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = caloriesError != null,
                supportingText = {
                    caloriesError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // ---- Carbs ----
            OutlinedTextField(
                value = carbs,
                onValueChange = {
                    carbs = it.filter { ch -> ch.isDigit() || ch == '-' }
                    carbsError = null
                },
                label = { Text("Carbs (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = carbsError != null,
                supportingText = {
                    carbsError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // ---- Fat ----
            OutlinedTextField(
                value = fat,
                onValueChange = {
                    fat = it.filter { ch -> ch.isDigit() || ch == '-' }
                    fatError = null
                },
                label = { Text("Fat (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = fatError != null,
                supportingText = {
                    fatError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // ---- Protein ----
            OutlinedTextField(
                value = protein,
                onValueChange = {
                    protein = it.filter { ch -> ch.isDigit() || ch == '-' }
                    proteinError = null
                },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = proteinError != null,
                supportingText = {
                    proteinError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
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
            Text("Rating", style = MaterialTheme.typography.titleMedium)
            RatingBar(
                rating = rating,
                onRatingSelected = { rating = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ---- Save Button ----
            Button(
                onClick = {
                    var hasError = false

                    if (title.isBlank()) {
                        titleError = "Meal title is required"
                        hasError = true
                    }

                    val caloriesInt = calories.toIntOrNull()
                    if (caloriesInt == null || caloriesInt <= 0) {
                        caloriesError = "Calories must be greater than 0"
                        hasError = true
                    }

                    val carbsInt = carbs.toIntOrNull() ?: 0
                    val fatInt = fat.toIntOrNull() ?: 0
                    val proteinInt = protein.toIntOrNull() ?: 0

                    if (carbsInt < 0) {
                        carbsError = "Carbs dürfen nicht negativ sein."
                        hasError = true
                    }

                    if (fatInt < 0) {
                        fatError = "Fat darf nicht negativ sein."
                        hasError = true
                    }

                    if (proteinInt < 0) {
                        proteinError = "Protein darf nicht negativ sein."
                        hasError = true
                    }

                    if (hasError) return@Button

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

            // ---- Macro Error Dialog (fallback) ----
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
