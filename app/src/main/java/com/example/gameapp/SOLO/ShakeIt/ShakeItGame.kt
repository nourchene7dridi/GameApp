package com.example.gameapp.SOLO.ShakeIt

import android.os.*
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.gameapp.R
import kotlin.math.sqrt
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class ShakeItGame : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeText: TextView
    private var shakeCount = 0
    private var startTime: Long = 0
    private var gameRunning = false

    private val shakeThreshold = 12f // À ajuster selon les tests

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (!gameRunning) return

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt(x * x + y * y + z * z)
            if (acceleration > shakeThreshold) {
                shakeCount++
                shakeText.text = "Shake count: $shakeCount"
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shake_it)

        shakeText = findViewById(R.id.shakeText)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        startGame()
    }

    private fun startGame() {
        shakeCount = 0
        gameRunning = true
        startTime = System.currentTimeMillis()
        shakeText.text = "Shake it!"

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        Handler(Looper.getMainLooper()).postDelayed({
            gameRunning = false
            sensorManager.unregisterListener(sensorListener)
            shakeText.text = "Temps écoulé ! Score: $shakeCount"
        }, 10000) // 10 secondes
    }

    override fun onPause() {
        super.onPause()
        if (gameRunning) {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    override fun onResume() {
        super.onResume()
        if (gameRunning) {
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }
}
