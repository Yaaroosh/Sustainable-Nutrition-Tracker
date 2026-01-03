package com.example.sustainablenutritiontracker.ui.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sustainablenutritiontracker.ui.components.RatingBar
import com.example.sustainablenutritiontracker.ui.components.SwitchRow

private val VioletPrimary = Color(0xFF7C4DFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealScreen(
    mealId: Long,
    viewModel: MealListViewModel,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val meals by viewModel.meals.collectAsState()
    val meal = meals.firstOrNull { it.id == mealId } ?: return

    var title by remember { mutableStateOf(meal.title) }
    var calories by remember { mutableStateOf(meal.calories.toString()) }
    var carbs by remember { mutableStateOf(meal.carbs.toString()) }
    var fat by remember { mutableStateOf(meal.fat.toString()) }
    var protein by remember { mutableStateOf(meal.protein.toString()) }
    var rating by remember { mutableIntStateOf(meal.rating) }

    var isVegan by remember { mutableStateOf(meal.isVegan) }
    var vegetarian by remember { mutableStateOf(meal.vegetarian) }
    var containsMeat by remember { mutableStateOf(meal.containsMeat) }

    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
    var mealType by remember { mutableStateOf(meal.mealType) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Meal", fontWeight = FontWeight.Bold, color = VioletPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = VioletPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White) {
                Button(
                    onClick = {
                        val updatedMeal = meal.copy(
                            title = title.trim(),
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
                        viewModel.editMeal(updatedMeal)
                        onSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                ) {
                    Text("Save changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Meal title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        focusedLabelColor = VioletPrimary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter(Char::isDigit) },
                    label = { Text("Calories (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        focusedLabelColor = VioletPrimary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it.filter(Char::isDigit) },
                    label = { Text("Carbs (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        focusedLabelColor = VioletPrimary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it.filter(Char::isDigit) },
                    label = { Text("Fat (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        focusedLabelColor = VioletPrimary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it.filter(Char::isDigit) },
                    label = { Text("Protein (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        focusedLabelColor = VioletPrimary
                    )
                )
            }

            item {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal type") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VioletPrimary,
                            focusedLabelColor = VioletPrimary
                        )
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        mealTypes.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = { mealType = it; expanded = false }
                            )
                        }
                    }
                }
            }

            item {
                Text("Rating", style = MaterialTheme.typography.titleMedium)
                RatingBar(rating = rating, onRatingSelected = { rating = it })
            }

            item { Text("Dietary options", style = MaterialTheme.typography.titleMedium) }

            item {
                SwitchRow("Vegan", isVegan) {
                    isVegan = it
                    if (it) { vegetarian = true; containsMeat = false }
                }
            }
            item {
                SwitchRow("Vegetarian", vegetarian) {
                    vegetarian = it
                    if (it) { isVegan = false; containsMeat = false }
                }
            }
            item {
                SwitchRow("Contains meat", containsMeat) {
                    containsMeat = it
                    if (it) { vegetarian = false; isVegan = false }
                }
            }

            item { Spacer(Modifier.height(90.dp)) }
        }
    }
}
