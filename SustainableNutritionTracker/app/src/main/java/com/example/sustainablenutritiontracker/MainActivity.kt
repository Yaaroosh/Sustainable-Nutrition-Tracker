package com.example.sustainablenutritiontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sustainablenutritiontracker.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}
//ok was ich noch finde das ist das ein zurück button fehlt und das jedes kalorien pro 100g eingibt und wie viel gram vom essen ist damit man rechnen kann wie viel calorien es ist