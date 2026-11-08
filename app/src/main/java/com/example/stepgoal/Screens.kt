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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

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

class StepViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext

    var dailyGoal by mutableIntStateOf(StepPrefs.getGoal(appContext))
        private set

    var stepsToday by mutableIntStateOf(6420)
        private set

    fun updateGoal(newGoal: Int) {
        dailyGoal = newGoal
        StepPrefs.setGoal(appContext, newGoal)
    }
}

@Composable
fun HomeScreen(
    viewModel: StepViewModel = viewModel(),
    onGoToGoals: () -> Unit,
    onGoToHistory: () -> Unit
) {
    val context = LocalContext.current
    val steps = viewModel.stepsToday
    val goal = viewModel.dailyGoal

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
fun GoalsScreen(
    viewModel: StepViewModel = viewModel(),
    onBack: () -> Unit
) {
    var goalText by remember { mutableStateOf(viewModel.dailyGoal.toString()) }

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
            viewModel.updateGoal(newGoal)
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