package com.example.sustainablenutritiontracker.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val mealType: String,
    val imagePath: String? = null,
    val rating: Int = 0,
    val environmentalScore: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val containsMeat: Boolean,
    val isVegan: Boolean,
    val vegetarian: Boolean
) {

    init {
        // DEBUG LOGGING
        val macroCalories = carbs * 4 + protein * 4 + fat * 9
        Log.d(
            "MealDebug", 
            "Meal: $title | cal=$calories | macro=$macroCalories | vegan=${if(isVegan)"✓" else "✗"}"
        )

        // ⚠️ MINIMAL VALIDATION - KEINE CRASHES!
        require(title.isNotBlank()) { "Titel darf nicht leer sein." }
        
        val allowedMealTypes = listOf("breakfast", "lunch", "dinner", "snack")
        require(mealType in allowedMealTypes) { "Ungültiger Meal-Typ: $allowedMealTypes" }
        
        require(calories > 0) { "Kalorien müssen > 0 sein." }
        require(carbs >= 0 && fat >= 0 && protein >= 0) { "Makros >= 0" }
        require(rating in 0..5) { "Rating 0-5" }

        // ✅ WARNUNGEN statt CRASH (realistisch für Nutrition Tracker)
        if (calories > 5000) Log.w("Meal", "Hohe Kalorien: $calories")
        if (macroCalories > calories * 2) Log.w("Meal", "Makro-Kalorien höher als Gesamt: $macroCalories > ${calories}")
        if (macroCalories < calories * 0.1) Log.w("Meal", "Makro-Kalorien zu niedrig")
    }

    fun getCO2Impact(): Double {
        return when {
            isVegan -> 0.7        // 700g CO2 per vegan meal
            vegetarian -> 0.85    // 850g CO2 per vegetarian meal
            else -> 1.4           // 1400g CO2 per meal with meat
        }
    }

    fun getDietTypeLabel(): String {
        return when {
            isVegan -> "vegan"
            vegetarian -> "vegetarian"
            else -> "regular"
        }
    }
}
