package com.example.smarthome

import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHomeApp()
        }
    }
}

@Composable
fun SmartHomeApp() {
    // Set up Navigation
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomePage(navController = navController) }
        composable("control") { SmartHomeControl() }
    }
}

@Composable
fun HomePage(navController: NavController) {
    // Gradient background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF6a11cb), Color(0xFF2575fc)) // Gradient colors for a nice effect
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Add Image for Home page with border and rounded corners
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()  // Adjust width to fit the screen
                .height(300.dp)  // Adjust height for a more centered look
                .padding(2.dp),  // Padding inside the card
            elevation = CardDefaults.cardElevation(10.dp) // Correct way to set elevation
        ) {
            Image(
                painter = painterResource(id = R.drawable.img),  // Replace with your image resource
                contentDescription = "Smart Home",
                modifier = Modifier
                    .fillMaxSize()  // Fill the card size
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))  // Border with rounded corners
            )
        }

        // Title Text
        Text(
            text = "Welcome to Smart Home",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Subtitle Text for additional description
        Text(
            text = "Control your environment with ease",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Start Button with enhanced style
        Button(
            onClick = { navController.navigate("control") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2575fc)),
            elevation = ButtonDefaults.buttonElevation(10.dp)
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),  // Changed to titleMedium
                color = Color.White
            )
        }
    }
}

@Composable
fun SmartHomeControl() {
    // Correct 4-digit PIN code
    val correctPin = "1234"

    // Initialize state for entered PIN code
    var enteredCode by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf(22) }  // Temperature in Celsius
    var humidity by remember { mutableStateOf(60) }  // Humidity in percentage
    var distance by remember { mutableStateOf(100) }  // Distance in cm
    var fanSpeed by remember { mutableStateOf(1) }  // Fan speed: 1 = low, 2 = medium, 3 = high

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val toneGenerator = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)

    // Handle key press and PIN validation
    fun onKeyPress(key: String) {
        // Play a sound for each key press
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)

        // Add the pressed key to the entered code
        if (enteredCode.length < 4) {
            enteredCode += key
        }

        // If 4 digits are entered, validate the PIN
        if (enteredCode.length == 4) {
            try {
                if (enteredCode == correctPin) {
                    // Correct PIN: play rightpassword sound, success vibration, and Toast
                    val mediaPlayer = MediaPlayer.create(context, R.raw.rightpassword)
                    mediaPlayer?.start()
                    Toast.makeText(context, "Correct PIN!", Toast.LENGTH_SHORT).show()
                } else {
                    // Incorrect PIN: play wrongpassword sound, failure vibration, and Toast
                    val mediaPlayer = MediaPlayer.create(context, R.raw.wrongpassword)
                    mediaPlayer?.start()
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle exceptions gracefully
                e.printStackTrace()
            }
            // Reset entered code after validation
            enteredCode = ""
        }
    }

    // Handle sensor cases (temperature, and ultrasonic distance)
    fun handleSensorCases() {
        try {
            // Check if temperature is too high, activate alarm using ToneGenerator
            if (temperature > 30) {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150)  // High temperature tone
            }
            if (temperature > 50) {
                val mediaPlayer = MediaPlayer.create(context, R.raw.firealarm)
                mediaPlayer?.start()
                Toast.makeText(context, "Fire Alarm Triggered!", Toast.LENGTH_SHORT).show()
            }

            // Check if ultrasonic distance is below threshold, warn about proximity using ToneGenerator
            if (distance < 50) {
                val mediaPlayer = MediaPlayer.create(context, R.raw.theftalarm)
                mediaPlayer?.start()
                Toast.makeText(context, "Theft Alarm Triggered!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any potential exceptions
            e.printStackTrace()
        }
    }

    // Control the fan speed based on temperature
    fun updateFanSpeed() {
        // If temperature is 0°C, turn off the fan
        if (temperature == 0) {
            fanSpeed = 0
            toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150) // Sound when fan is turned off
            Toast.makeText(context, "Fan Turned Off", Toast.LENGTH_SHORT).show()
        } else {
            // Set fan speed based on temperature
            val newFanSpeed = when {
                temperature < 20 -> 1  // Low temperature, low fan speed
                temperature in 20..30 -> 2  // Medium temperature, medium fan speed
                else -> 3  // High temperature, high fan speed
            }

            if (newFanSpeed != fanSpeed) {
                // If fan speed has changed, play a sound and show a Toast
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                fanSpeed = newFanSpeed
                Toast.makeText(context, "Fan Speed: $fanSpeed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Run sensor checks and update fan speed based on temperature
    handleSensorCases()
    updateFanSpeed()

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {  // Light gray background
        // Display Entered Code
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Entered Code: $enteredCode",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad for entering PIN code
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(onClick = { onKeyPress("1") }) { Text(text = "1") }
            Button(onClick = { onKeyPress("2") }) { Text(text = "2") }
            Button(onClick = { onKeyPress("3") }) { Text(text = "3") }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(onClick = { onKeyPress("4") }) { Text(text = "4") }
            Button(onClick = { onKeyPress("5") }) { Text(text = "5") }
            Button(onClick = { onKeyPress("6") }) { Text(text = "6") }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(onClick = { onKeyPress("7") }) { Text(text = "7") }
            Button(onClick = { onKeyPress("8") }) { Text(text = "8") }
            Button(onClick = { onKeyPress("9") }) { Text(text = "9") }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(onClick = { onKeyPress("0") }) { Text(text = "0") }
        }

        // Temperature reading and controls
        Spacer(modifier = Modifier.height(16.dp))

        // Temperature reading
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Temperature: $temperature°C", style = MaterialTheme.typography.bodyLarge)
                Row {
                    Button(onClick = { temperature += 1 }) { Text(text = "Increase Temperature") }
                    Button(onClick = { temperature -= 1 }) { Text(text = "Decrease Temperature") }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fan Speed
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Fan Speed: $fanSpeed", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Distance reading
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Distance: $distance cm", style = MaterialTheme.typography.bodyLarge)
                Row {
                    Button(onClick = { distance += 10 }) { Text(text = "Increase Distance") }
                    Button(onClick = { distance -= 10 }) { Text(text = "Decrease Distance") }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartHomeApp()
}
