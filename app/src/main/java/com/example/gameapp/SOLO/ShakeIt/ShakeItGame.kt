package com.example.gameapp.SOLO.ShakeIt

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.example.gameapp.R
import kotlin.math.sqrt

class ShakeItGame : ComponentActivity(), SensorEventListener {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var shakeScore = 0
    private lateinit var shakeText: TextView
    private lateinit var shakeGif: ImageView
    private lateinit var timerText: TextView
    private var isGameRunning = true
    private var lastShakeTime = 0L
    private val cooldown = 500  // ms entre deux secousses valides

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shake_it)

        // Initialisation
        shakeText = findViewById(R.id.shakeText)
        shakeGif = findViewById(R.id.shakeGif)
        timerText = findViewById(R.id.timerText)

        // Affichage du gif avec Glide
        Glide.with(this).asGif().load(R.drawable.shaker).into(shakeGif)

        // Capteurs
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Son de 7 sec uniquement donc faudra le rejouer
        mediaPlayer = MediaPlayer.create(this, R.raw.effet_shaker)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Timer de fin de jeu
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = "Temps : $secondsLeft"
            }

            override fun onFinish() {
                isGameRunning = false
                timerText.text = "Temps : 0"
                shakeText.text = "Score final : $shakeScore"

                // Arrêter l'animation du gif ou le rendre statique
                Glide.with(this@ShakeItGame).clear(shakeGif)
                shakeGif.setImageResource(R.drawable.shaker)

                // Stopper le son
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

        // Arrêt et libération du MediaPlayer si encore actif
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isGameRunning || event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

        if (acceleration > 5) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > cooldown) {
                lastShakeTime = currentTime
                shakeScore++
                shakeText.text = "Score : $shakeScore"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
