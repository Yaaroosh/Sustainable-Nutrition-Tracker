package com.example.sustainablenutritiontracker.ui.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sustainablenutritiontracker.data.model.TodayMealEntity

private val VioletPrimary = Color(0xFF7C4DFF)
private val CardBg = Color(0xFFF1ECF8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onBack: () -> Unit,
    onPickMeal: (mealType: String) -> Unit
) {
    val date by viewModel.date.collectAsState()
    val items by viewModel.todayMeals.collectAsState()
    val totals by viewModel.totals.collectAsState()

    // NEU: CO2 Popup State
    val co2PopupData by viewModel.showCO2Popup.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Today · $date", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.previousDay() }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous day")
                    }
                    IconButton(onClick = { viewModel.nextDay() }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next day")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = VioletPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {

            item {
                TodayInlineSummary(
                    calories = totals.calories,
                    carbs = totals.carbs,
                    fat = totals.fat,
                    protein = totals.protein
                )
            }

            item { SectionHeader("Breakfast") { onPickMeal("breakfast") } }
            items(items.filter { it.mealType == "breakfast" }, key = { it.id }) { e ->
                TodayMealRow(entity = e, onDelete = { viewModel.deleteTodayMeal(e.id) })
            }

            item { SectionHeader("Lunch") { onPickMeal("lunch") } }
            items(items.filter { it.mealType == "lunch" }, key = { it.id }) { e ->
                TodayMealRow(entity = e, onDelete = { viewModel.deleteTodayMeal(e.id) })
            }

            item { SectionHeader("Dinner") { onPickMeal("dinner") } }
            items(items.filter { it.mealType == "dinner" }, key = { it.id }) { e ->
                TodayMealRow(entity = e, onDelete = { viewModel.deleteTodayMeal(e.id) })
            }

            item { SectionHeader("Snack") { onPickMeal("snack") } }
            items(items.filter { it.mealType == "snack" }, key = { it.id }) { e ->
                TodayMealRow(entity = e, onDelete = { viewModel.deleteTodayMeal(e.id) })
            }

            item { Spacer(Modifier.height(6.dp)) }
        }
    }

    // NEU: CO2 Impact Popup
    co2PopupData?.let { data ->
        com.example.sustainablenutritiontracker.ui.environmental.CO2ImpactPopup(
            data = data,
            onDismiss = { viewModel.dismissCO2Popup() }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.weight(1f))

        Surface(
            onClick = onAdd,
            shape = CircleShape,
            color = CardBg,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = VioletPrimary
                )
            }
        }
    }
}

@Composable
private fun TodayMealRow(
    entity: TodayMealEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entity.title, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${entity.calories} kcal • ${entity.grams} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
