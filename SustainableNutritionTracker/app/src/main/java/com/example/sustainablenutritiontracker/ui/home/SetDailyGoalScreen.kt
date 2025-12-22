package com.example.sustainablenutritiontracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sustainablenutritiontracker.ui.viewmodel.DailyGoalViewModel

private val VioletPrimary = Color(0xFF7C4DFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDailyGoalScreen(
    viewModel: DailyGoalViewModel,
    onNavigateBack: () -> Unit
) {
    val dailyGoals by viewModel.dailyGoals.collectAsState()

    var caloriesInput by remember { mutableStateOf(dailyGoals.caloriesLimit.toString()) }
    var carbsInput by remember { mutableStateOf(dailyGoals.carbsLimit.toString()) }
    var fatInput by remember { mutableStateOf(dailyGoals.fatLimit.toString()) }
    var proteinInput by remember { mutableStateOf(dailyGoals.proteinLimit.toString()) }

    LaunchedEffect(dailyGoals) {
        caloriesInput = dailyGoals.caloriesLimit.toString()
        carbsInput = dailyGoals.carbsLimit.toString()
        fatInput = dailyGoals.fatLimit.toString()
        proteinInput = dailyGoals.proteinLimit.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Set Daily Goals",
                        fontWeight = FontWeight.Bold,
                        color = VioletPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = VioletPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "What are your daily nutrition goals?",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            GoalInputField(
                label = "Calories (kcal)",
                value = caloriesInput,
                onValueChange = { caloriesInput = it }
            )

            GoalInputField(
                label = "Carbs (g)",
                value = carbsInput,
                onValueChange = { carbsInput = it }
            )

            GoalInputField(
                label = "Fat (g)",
                value = fatInput,
                onValueChange = { fatInput = it }
            )

            GoalInputField(
                label = "Protein (g)",
                value = proteinInput,
                onValueChange = { proteinInput = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateGoals(
                        caloriesLimit = caloriesInput.toIntOrNull() ?: 2000,
                        carbsLimit = carbsInput.toIntOrNull() ?: 250,
                        fatLimit = fatInput.toIntOrNull() ?: 70,
                        proteinLimit = proteinInput.toIntOrNull() ?: 150
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VioletPrimary
                )
            ) {
                Text("Done", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GoalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VioletPrimary,
            focusedLabelColor = VioletPrimary
        ),
        singleLine = true
    )
}
