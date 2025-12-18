package com.example.sustainablenutritiontracker.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.MealViewModel


private val VioletPrimary = Color(0xFF7C4DFF)
private val VioletCalories = Color(0xFFB388FF)
private val VioletCarbs = Color(0xFF9575CD)
private val VioletProtein = Color(0xFF5E35B1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dailyGoalViewModel: DailyGoalViewModel,
    mealViewModel: MealViewModel,
    onNavigateToList: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSetGoals: () -> Unit
) {
    // ViewModels auslesen
    val dailyGoals by dailyGoalViewModel.dailyGoals.collectAsState()
    val nutritionTotals by mealViewModel.nutritionTotals.collectAsState()

    // Dynamische Werte aus Database
    val caloriesLimit = dailyGoals.caloriesLimit
    val carbsLimit = dailyGoals.carbsLimit
    val fatLimit = dailyGoals.fatLimit
    val proteinLimit = dailyGoals.proteinLimit

    val caloriesTaken = nutritionTotals.calories
    val carbsTaken = nutritionTotals.carbs
    val fatTaken = nutritionTotals.fat
    val proteinTaken = nutritionTotals.protein

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Nutrition Tracker",
                        fontWeight = FontWeight.Bold,
                        color = VioletPrimary
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = VioletPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Meal",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFEDE7F6),
                            Color(0xFFF3E5F5),
                        )
                    )
                )
                .padding(padding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Today's Intake",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = VioletPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MacroCircle(
                    label = "Calories",
                    current = caloriesTaken,
                    limit = caloriesLimit,
                    color = VioletCalories,
                    icon = Icons.Default.Restaurant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroCircle(
                    label = "Carbs",
                    current = carbsTaken,
                    limit = carbsLimit,
                    color = VioletProtein,
                    icon = Icons.Default.LocalFireDepartment
                )

                MacroCircle(
                    label = "Fat",
                    current = fatTaken,
                    limit = fatLimit,
                    color = VioletProtein,
                    icon = Icons.Default.Opacity
                )

                MacroCircle(
                    label = "Protein",
                    current = proteinTaken,
                    limit = proteinLimit,
                    color = VioletProtein,
                    icon = Icons.Default.FitnessCenter
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            MacroDetailRow(
                title = "Calories",
                current = caloriesTaken,
                limit = caloriesLimit,
                color = VioletProtein,
                icon = Icons.Default.Restaurant
            )

            MacroDetailRow(
                title = "Carbs",
                current = carbsTaken,
                limit = carbsLimit,
                color = VioletProtein,
                icon = Icons.Default.LocalFireDepartment
            )

            MacroDetailRow(
                title = "Fat",
                current = fatTaken,
                limit = fatLimit,
                color = VioletProtein,
                icon = Icons.Default.Opacity
            )

            MacroDetailRow(
                title = "Protein",
                current = proteinTaken,
                limit = proteinLimit,
                color = VioletProtein,
                icon = Icons.Default.FitnessCenter
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NEU: Set Daily Goals Button
            OutlinedButton(
                onClick = onNavigateToSetGoals,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VioletPrimary
                ),
                border = BorderStroke(2.dp, VioletPrimary)
            ) {
                Text("Set Daily Goals")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToList,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VioletCalories
                ),
                border = BorderStroke(3.dp, VioletCalories)
            ) {
                Text("View Meal List")
            }
        }
    }
}

// MacroCircle und MacroDetailRow bleiben gleich
@Composable
fun MacroCircle(
    label: String,
    current: Int,
    limit: Int,
    color: Color,
    icon: ImageVector
) {
    val progress = current.toFloat() / limit.toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.size(92.dp),
                color = color,
                strokeWidth = 6.dp,
                trackColor = color.copy(alpha = 0.2f),
            )

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
fun MacroDetailRow(
    title: String,
    current: Int,
    limit: Int,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.10f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = color
            )

            Text(
                text = "$current / $limit",
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
