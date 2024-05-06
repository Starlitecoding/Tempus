package com.starlitecoding.tempus

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import com.example.compose.TempusTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        super.onCreate(savedInstanceState)
        setContent {
            TempusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                }
            }
        }
    }
}


@Composable
fun App(vibrator: Vibrator, modifier: Modifier = Modifier) {
    val timeValue = remember { mutableStateOf(0) }
    val sliderValue = remember { mutableStateOf(0) } // Initial slider value
    var isRunning = remember { mutableStateOf(false) }
    // Первая колонка занимает оставшееся место сверху
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerDisplay(timeValue.value)
    }

    // Вторая колонка занимает 75% экрана снизу
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 200.dp)
            .padding(horizontal = 64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        TimerControl(timeValue, sliderValue)
        Spacer(modifier = Modifier.size(16.dp))
        TimerStop(timeValue, sliderValue, isRunning)
    }

    var hasVibrated = false

    LaunchedEffect(key1 = timeValue) {
        while (true) {
            delay(100L) // Adjust delay as needed
            if (timeValue.value > 0) {
                timeValue.value -= 1
            } else {
                // Reset timer to slider value, vibrate on every completion
                timeValue.value = sliderValue.value
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }
}

@Composable
fun TimerDisplay(timeSeconds: Int, modifier: Modifier = Modifier) {
    val minutes = timeSeconds / 60
    val remainingSeconds = timeSeconds % 60

    Text(
        text = String.format("%02d:%02d", minutes, remainingSeconds),
        fontSize = 64.sp,

        )
}


@Composable
fun TimerControl(timeValue: MutableState<Int>, sliderValue: MutableState<Int>, modifier: Modifier = Modifier) {
    val maxValue = 600
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            value = sliderValue.value.toFloat(), // Access the actual value using .value
            onValueChange = { newValue ->
                sliderValue.value = newValue.toInt() // Update sliderValue using .value
                timeValue.value = sliderValue.value // Update timer value on slider change
            },
            valueRange = 0f..maxValue.toFloat(), // Диапазон значений ползунка (from 0 to 4 minutes)
            steps = (9) // Шаг изменения значения (1 minute)
        )
    }
}

@Composable
fun TimerStop(timeValue: MutableState<Int>, sliderValue: MutableState<Int>, isRunning: MutableState<Boolean>, modifier: Modifier = Modifier) {
    Button(modifier = Modifier
        .width(128.dp)
        .height(48.dp),
        onClick = {
            isRunning.value = false // Stop the timer cycle
            timeValue.value = 0 // Reset timer value
            sliderValue.value = 0 // Reset slider value
        }) {
        Text(text = stringResource(id = R.string.stopTimer), fontSize = 16.sp)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TempusTheme {

    }
}