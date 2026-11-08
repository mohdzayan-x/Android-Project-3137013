package com.example.stepgoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.stepgoal.ui.theme.StepGoalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepGoalTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onGoToGoals = { currentScreen = "goals" },
            onGoToHistory = { currentScreen = "history" }
        )
        "goals" -> GoalsScreen(
            onBack = { currentScreen = "home" }
        )
        "history" -> HistoryScreen(
            onBack = { currentScreen = "home" }
        )
    }
}