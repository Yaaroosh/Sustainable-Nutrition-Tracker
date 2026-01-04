package com.example.sustainablenutritiontracker.ui.environmental

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sustainablenutritiontracker.ui.today.TodayViewModel
import com.example.sustainablenutritiontracker.ui.viewmodel.CO2PopupData
import kotlin.math.abs
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign

private val VioletPrimary = Color(0xFF7C4DFF)
private val GreenSuccess = Color(0xFF4CAF50)
private val RedError = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentalImpactScreen(
    todayViewModel: TodayViewModel,
    onBack: () -> Unit
) {
    val totalCO2 by todayViewModel.totalCO2Saved.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Environmental Impact",
                        fontWeight = FontWeight.Bold,
                        color = VioletPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = VioletPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Environment",
                modifier = Modifier.size(80.dp),
                tint = if (totalCO2 >= 0) GreenSuccess else RedError
            )

            Text(
                text = "Today's CO₂ Impact",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VioletPrimary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalCO2 >= 0)
                        GreenSuccess.copy(alpha = 0.1f)
                    else RedError.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (totalCO2 >= 0) "CO₂ Saved" else "Extra CO₂",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalCO2 >= 0) GreenSuccess else RedError
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${String.format("%.2f", abs(totalCO2))} kg",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalCO2 >= 0) GreenSuccess else RedError
                    )
                }
            }

            Text(
                text = "Compared to eating regular meals today",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Motivierender Text
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalCO2 >= 0)
                        GreenSuccess.copy(alpha = 0.15f)
                    else Color(0xFFFFEBEE)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when {
                            totalCO2 >= 1.0 -> "🌟 Amazing! Keep up the great work!"
                            totalCO2 > 0 -> "👍 Good job! You're making a difference!"
                            totalCO2 == 0.0 -> "💚 Try eating more plant-based meals!"
                            else -> "🌱 Consider eating more vegetarian or vegan meals!"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalCO2 >= 0) GreenSuccess else Color(0xFFD32F2F)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            val co2History by todayViewModel.last7DaysCO2.collectAsState()

            val chartData = co2History.mapIndexed { index, value ->
                val date = java.time.LocalDate.now().minusDays(index.toLong())
                val dayLabel = date.dayOfWeek.name.take(1)
                dayLabel to value
            }.reversed()

            @Composable
            fun CO2Last7DaysChart(
                dailyValues: List<Pair<String, Double>>,
                modifier: Modifier = Modifier
            ) {
                val maxValue = (dailyValues.maxOfOrNull { kotlin.math.abs(it.second) } ?: 1.0).coerceAtLeast(1.0)
                val scaleValues = listOf(
                    String.format("%.1f", maxValue),
                    String.format("%.1f", maxValue / 2),
                    "0.0"
                )

                Column(modifier = modifier) {
                    Text(
                        text = "Last 7 Days CO₂ Impact (kg)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VioletPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(bottom = 24.dp, end = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            scaleValues.forEach { scale ->
                                Text(text = scale, fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            dailyValues.forEach { (dayLabel, value) ->
                                val proportion = (kotlin.math.abs(value) / maxValue).toFloat().coerceIn(0.02f, 1.0f)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    Text(
                                        text = String.format("%.1f", value),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (value >= 0) GreenSuccess else RedError
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(
                                        modifier = Modifier
                                            .weight(1f, fill = true)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(22.dp)
                                                .fillMaxHeight(proportion)
                                                .background(
                                                    color = if (value >= 0) GreenSuccess else RedError,
                                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                                )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = dayLabel,
                                        fontSize = 12.sp,
                                        color = if (dayLabel == java.time.LocalDate.now().dayOfWeek.name.take(1))
                                            VioletPrimary else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            CO2Last7DaysChart(
                dailyValues = chartData,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text(
                text = "CO₂ per Meal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VioletPrimary
            )

            InfoCard(
                title = "Vegan Meal",
                co2Value = "0.7 kg CO₂",
                description = "Saves 0.7 kg per meal",
                color = GreenSuccess
            )

            InfoCard(
                title = "Vegetarian Meal",
                co2Value = "0.85 kg CO₂",
                description = "Saves 0.55 kg per meal",
                color = Color(0xFF8BC34A)
            )

            InfoCard(
                title = "Regular Meal",
                co2Value = "1.4 kg CO₂",
                description = "Baseline (with meat)",
                color = Color(0xFFFF9800)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    co2Value: String,
    description: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = co2Value,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun CO2ImpactPopup(
    data: CO2PopupData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = if (data.isSaved) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(72.dp)
            )
        },
        title = {
            Text(
                text = if (data.isSaved) "🌱 CO₂ Saved!" else "🏭 CO₂ Produced",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = "${String.format("%.2f", data.amount)} kg CO₂",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (data.isSaved) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (data.isSaved) "saved" else "produced",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "by choosing a ${data.dietType} meal",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (data.isSaved)
                        "Great choice for the environment! 🌍"
                    else "Consider plant-based alternatives 🌱",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (data.isSaved) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            ) {
                Text("Got it!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}
