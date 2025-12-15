package com.example.sustainablenutritiontracker.ui.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.sustainablenutritiontracker.ui.components.RatingBar
import com.example.sustainablenutritiontracker.ui.components.SwitchRow
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
    var rating by remember { mutableIntStateOf(0) }

    var isVegan by remember { mutableStateOf(false) }
    var vegetarian by remember { mutableStateOf(false) }
    var containsMeat by remember { mutableStateOf(false) }

    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
    var mealType by remember { mutableStateOf(mealTypes.first()) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Add Meal") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // SCROLLBARER INHALT
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Meal title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it.filter(Char::isDigit) },
                        label = { Text("Calories") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it.filter(Char::isDigit) },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it.filter(Char::isDigit) },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it.filter(Char::isDigit) },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = mealType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Meal type") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            mealTypes.forEach {
                                DropdownMenuItem(
                                    text = { Text(it) },
                                    onClick = {
                                        mealType = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Rating", style = MaterialTheme.typography.titleMedium)
                    RatingBar(rating = rating, onRatingSelected = { rating = it })
                }

                item {
                    Text("Dietary options", style = MaterialTheme.typography.titleMedium)
                }

                item {
                    SwitchRow("Vegan", isVegan) {
                        isVegan = it
                        if (it) {
                            vegetarian = true
                            containsMeat = false
                        }
                    }
                }

                item {
                    SwitchRow("Vegetarian", vegetarian) {
                        vegetarian = it
                        if (it) {
                            isVegan = false
                            containsMeat = false
                        }
                    }
                }

                item {
                    SwitchRow("Contains meat", containsMeat) {
                        containsMeat = it
                        if (it) {
                            vegetarian = false
                            isVegan = false
                        }
                    }
                }
            }

            //  FESTER SAVE BUTTON (IMMER SICHTBAR)
            Button(
                onClick = {
                    val meal = Meal(
                        title = title,
                        calories = calories.toIntOrNull() ?: 0,
                        carbs = carbs.toIntOrNull() ?: 0,
                        fat = fat.toIntOrNull() ?: 0,
                        protein = protein.toIntOrNull() ?: 0,
                        mealType = mealType,
                        rating = rating,
                        isVegan = isVegan,
                        vegetarian = vegetarian,
                        containsMeat = containsMeat
                    )
                    viewModel.addMeal(meal)
                    onSave()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Save meal")
            }
        }
    }
}


