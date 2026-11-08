package com.example.stepgoal

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// prefs helper so we don't keep writing the same key everywhere
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

// viewmodel so the goal and steps stay in one place
class StepViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext

    var dailyGoal by mutableIntStateOf(StepPrefs.getGoal(appContext))
        private set

    // still fake for now, will change later when we add the sensor
    var stepsToday by mutableIntStateOf(6_420)
        private set

    fun updateGoal(newGoal: Int) {
        // don't let people put crazy numbers
        val clamped = newGoal.coerceIn(1_000, 50_000)
        dailyGoal = clamped
        StepPrefs.setGoal(appContext, clamped)
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
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Home", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))

        Text("Steps today: $steps / $goal", fontSize = 20.sp)

        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.75f)
        )

        Spacer(Modifier.height(32.dp))

        Button(onClick = onGoToGoals) { Text("Set Goal") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onGoToHistory) { Text("View History") }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            val message = "I've walked $steps steps today! (Goal: $goal)"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
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
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Daily Goal", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = goalText,
            onValueChange = {
                goalText = it
                error = null // clear the error when they start typing again
            },
            label = { Text("Steps") },
            isError = error != null,
            supportingText = error?.let { { Text(it) } }
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            val parsed = goalText.toIntOrNull()
            when {
                parsed == null -> error = "Enter a number"
                parsed < 1_000 -> error = "Minimum is 1,000 steps"
                parsed > 50_000 -> error = "Maximum is 50,000 steps"
                else -> {
                    viewModel.updateGoal(parsed)
                    onBack()
                }
            }
        }) {
            Text("Save Goal")
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Cancel") }
    }
}

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    // just hard coded for now, will make it real later
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("History", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text("Mon: 8,200 steps", fontSize = 18.sp)
        Text("Tue: 9,500 steps", fontSize = 18.sp)
        Text("Wed: 6,100 steps", fontSize = 18.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack) { Text("Back to Home") }
    }
}