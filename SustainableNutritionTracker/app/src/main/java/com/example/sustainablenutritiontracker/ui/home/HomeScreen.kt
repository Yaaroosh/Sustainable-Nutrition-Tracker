package com.example.sustainablenutritiontracker.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Eco
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sustainablenutritiontracker.ui.today.TodayViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel

private val VioletPrimary = Color(0xFF7C4DFF)
private val VioletCalories = Color(0xFFB388FF)
private val VioletCarbs = Color(0xFF9575CD)
private val VioletProtein = Color(0xFF5E35B1)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    dailyGoalViewModel: DailyGoalViewModel,
    todayViewModel: TodayViewModel,
    onNavigateToList: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToSetGoals: () -> Unit,
    onNavigateToToday: () -> Unit,
    onNavigateToEnvironmental: () -> Unit
) {
    val dailyGoals by dailyGoalViewModel.dailyGoals.collectAsState()
    val totals by todayViewModel.totalsNow.collectAsState()

    val caloriesLimit = dailyGoals.caloriesLimit
    val carbsLimit = dailyGoals.carbsLimit
    val fatLimit = dailyGoals.fatLimit
    val proteinLimit = dailyGoals.proteinLimit

    val caloriesTaken = totals.calories
    val carbsTaken = totals.carbs
    val fatTaken = totals.fat
    val proteinTaken = totals.protein

    val contentWidth = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)

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
                .verticalScroll(rememberScrollState())
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
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header
            Text(
                text = "Daily Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = VioletPrimary,
                modifier = contentWidth
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val streak by todayViewModel.streak.collectAsState()
            Text(
                text = "Current sustainability streak: $streak days"
            )

            // 🟢 ENVIRONMENT SCORE BAR - NEU
            val environmentScore by todayViewModel.environmentScore.collectAsState()
            EnvironmentScoreBar(percentage = environmentScore.percentage)
            // 🟢 ENDE

            Spacer(modifier = Modifier.height(16.dp))

            // Big calories circle
            Box(
                modifier = contentWidth,
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

            // Macro circles row
            Row(
                modifier = contentWidth,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroCircle(
                    label = "Carbs",
                    current = carbsTaken,
                    limit = carbsLimit,
                    color = VioletCarbs,
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

            Spacer(modifier = Modifier.height(26.dp))

            // Detail rows (values)
            Column(modifier = contentWidth) {
                MacroDetailRow(
                    title = "Calories",
                    current = caloriesTaken,
                    limit = caloriesLimit,
                    color = VioletCalories,
                    icon = Icons.Default.Restaurant
                )
                MacroDetailRow(
                    title = "Carbs",
                    current = carbsTaken,
                    limit = carbsLimit,
                    color = VioletCarbs,
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToToday,
                modifier = contentWidth.height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VioletCarbs
                ),
                border = BorderStroke(2.dp, VioletCarbs)
            ) {
                Text("Go to Today", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onNavigateToSetGoals,
                modifier = contentWidth.height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VioletPrimary
                ),
                border = BorderStroke(2.dp, VioletPrimary)
            ) {
                Text("Set Daily Goals", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onNavigateToList,
                modifier = contentWidth.height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = VioletCalories
                ),
                border = BorderStroke(2.dp, VioletCalories)
            ) {
                Text("View Meal List", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToEnvironmental,
                modifier = contentWidth.height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4CAF50)
                ),
                border = BorderStroke(2.dp, Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.Eco, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Environmental Impact", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun EnvironmentScoreBar(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val safePercentage = percentage.coerceIn(0, 100)
    
    val feedbackText = when {
        safePercentage == 0 -> "No meals yet today"
        safePercentage < 25 -> "Your environment score is low. Try more plant-based meals!"
        safePercentage < 50 -> "Improving! Add more vegan/vegetarian options."
        safePercentage < 75 -> "Good job! You're eating sustainably."
        safePercentage < 100 -> "Almost perfect! One more plant-based meal!"
        else -> "Perfect score! Keep up the great work! 🌱"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "${safePercentage}%",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VioletPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(20.dp)
                .background(
                    Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(safePercentage / 100f)
                    .fillMaxHeight()
                    .background(
                        VioletPrimary,
                        RoundedCornerShape(10.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = feedbackText,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MacroCircle(
    label: String,
    current: Int,
    limit: Int,
    color: Color,
    icon: ImageVector
) {
    val safeLimit = if (limit <= 0) 1 else limit
    val progress = current.toFloat() / safeLimit.toFloat()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
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
    val safeLimit = if (limit <= 0) 1 else limit

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
                text = "$current / $safeLimit",
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
