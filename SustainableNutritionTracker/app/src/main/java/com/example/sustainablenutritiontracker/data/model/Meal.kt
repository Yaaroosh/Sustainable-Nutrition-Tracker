package com.example.sustainablenutritiontracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val calories: Int,
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val mealType: String,
    val imagePath: String? = null,
    val rating: Int = 0,
    val date: Long = System.currentTimeMillis(),
) {

    init {
        // --- Basisvalidierung ---
        require(title.isNotBlank()) {
            "Titel darf nicht leer sein."
        }

        val allowedMealTypes = listOf("breakfast", "lunch", "dinner", "snack")
            require(mealType in allowedMealTypes) {
                "Ungültiger Meal-Typ. Erlaubt: $allowedMealTypes"
            }

        require(calories > 0) {
            "Kalorien müssen > 0 sein."
        }

        require(carbs >= 0) { "Carbs dürfen nicht negativ sein." }
        require(fat >= 0) { "Fat darf nicht negativ sein." }
        require(protein >= 0) { "Protein darf nicht negativ sein." }

        // --- Rating ---
        require(rating in 0..5) {
            "Rating muss zwischen 0 und 5 liegen."
        }

        // realistische Grenzen für Nährwerte
        require(calories <= 10000) {
            "Kalorienwert ist unrealistisch hoch (>10.000)."
        }

        require(carbs <= 1000) {
            "Carbs sind unrealistisch hoch (>1000 g)."
        }

        require(fat <= 500) {
            "Fat ist unrealistisch hoch (>500 g)."
        }

        require(protein <= 500) {
            "Protein ist unrealistisch hoch (>500 g)."
        }

        // --- Konsistenzprüfung ---
        val macroCalories = carbs * 4 + protein * 4 + fat * 9

        require(macroCalories <= calories * 1.5) {
            "Makronährstoffe sind unverhältnismäßig zu den Kalorien (zu viele Makro-Kalorien)."
        }

        require(macroCalories >= calories * 0.3) {
            "Makronährstoffe sind zu niedrig im Verhältnis zu den Kalorien (unplausibel)."
        }
    }
}
