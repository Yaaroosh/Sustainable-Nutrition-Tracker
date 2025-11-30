package com.example.sustainablenutritiontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sustainablenutritiontracker.ui.meal.MealListScreen
import com.example.sustainablenutritiontracker.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //AppNavigation()
            MealListScreen()
        }
    }
}