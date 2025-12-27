package com.example.sustainablenutritiontracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class InlineSummaryItem(
    val label: String,
    val value: String,
    val color: Color,
    val isPrimary: Boolean = false
)

@Composable
fun InlineSummary(
    items: List<InlineSummaryItem>,
    containerColor: Color,
    dividerColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEachIndexed { index, item ->
                InlineItem(
                    label = item.label,
                    value = item.value,
                    color = item.color,
                    bold = item.isPrimary
                )

                if (index < items.lastIndex) {
                    InlineDivider(dividerColor)
                }
            }
        }
    }
}

@Composable
private fun InlineItem(
    label: String,
    value: String,
    color: Color,
    bold: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (bold) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = color.copy(alpha = 0.85f)
            )
        } else {
            Text(
                text = "$label $value",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = color
            )
        }
    }
}

@Composable
private fun InlineDivider(color: Color) {
    Text(
        text = "·",
        modifier = Modifier.padding(horizontal = 8.dp),
        color = color,
        fontWeight = FontWeight.Bold
    )
}
