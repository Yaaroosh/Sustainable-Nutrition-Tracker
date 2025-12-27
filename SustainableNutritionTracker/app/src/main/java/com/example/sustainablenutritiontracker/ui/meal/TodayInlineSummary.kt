package com.example.sustainablenutritiontracker.ui.today

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.sustainablenutritiontracker.ui.components.InlineSummary
import com.example.sustainablenutritiontracker.ui.components.InlineSummaryItem

private val VioletPrimary = Color(0xFF7C4DFF)
private val VioletCarbs = Color(0xFF9575CD)
private val VioletFat = Color(0xFF6A5ACD)
private val VioletProtein = Color(0xFF5E35B1)
private val CardBg = Color(0xFFF1ECF8)

@Composable
fun TodayInlineSummary(
    calories: Int,
    carbs: Int,
    fat: Int,
    protein: Int
) {
    InlineSummary(
        containerColor = CardBg,
        dividerColor = VioletPrimary.copy(alpha = 0.35f),
        items = listOf(
            InlineSummaryItem(
                label = "kcal",
                value = calories.toString(),
                color = VioletPrimary,
                isPrimary = true
            ),
            InlineSummaryItem(
                label = "Carbs",
                value = "${carbs} g",
                color = VioletCarbs
            ),
            InlineSummaryItem(
                label = "Fat",
                value = "${fat} g",
                color = VioletFat
            ),
            InlineSummaryItem(
                label = "Protein",
                value = "${protein} g",
                color = VioletProtein
            )
        )
    )
}
