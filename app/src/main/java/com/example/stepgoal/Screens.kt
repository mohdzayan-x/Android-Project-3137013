package com.example.stepgoal

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object StepPrefs {
    private const val PREFS_NAME = "step_prefs"
    private const val KEY_GOAL = "daily_goal"
    private const val DEFAULT_GOAL = 10_000

    fun getGoal(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_GOAL, DEFAULT_GOAL)

    fun setGoal(context: Context, goal: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_GOAL, goal)
            .apply()
    }
}

@Composable
fun HomeScreen(onGoToGoals: () -> Unit, onGoToHistory: () -> Unit) {
    val context = LocalContext.current
    val goal = StepPrefs.getGoal(context)
    val steps = 6420

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text("Steps today: $steps / $goal", fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))

        Button(onClick = onGoToGoals) { Text("Set Goal") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoToHistory) { Text("View History") }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "I have walked $steps steps today! Goal: $goal")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }) {
            Text("Share Progress")
        }
    }
}

@Composable
fun GoalsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var goalText by remember { mutableStateOf(StepPrefs.getGoal(context).toString()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Daily Goal", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = goalText,
            onValueChange = { goalText = it },
            label = { Text("Steps") }
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            val newGoal = goalText.toIntOrNull() ?: 10000
            StepPrefs.setGoal(context, newGoal)
            onBack()
        }) {
            Text("Save Goal")
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Cancel") }
    }
}

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("History", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text("Mon: 8200 steps", fontSize = 18.sp)
        Text("Tue: 9500 steps", fontSize = 18.sp)
        Text("Wed: 6100 steps", fontSize = 18.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack) { Text("Back to Home") }
    }
}