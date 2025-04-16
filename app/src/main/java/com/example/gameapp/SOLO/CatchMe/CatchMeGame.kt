package com.example.gameapp.SOLO.CatchMe

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlin.random.Random
import com.example.gameapp.R


class CatchMeGame : ComponentActivity() {

    private lateinit var vibrator: Vibrator
    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private var answerIndex = -1

    private val patterns = arrayOf(
        longArrayOf(0, 200),              // Objet 1 : court
        longArrayOf(0, 500),              // Objet 2 : long
        longArrayOf(0, 100, 100, 100),    // Objet 3 : deux courtes
        longArrayOf(0, 300, 100, 300)     // Objet 4 : deux longues
    )

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catch_me)

        vibrator = getSystemService(Vibrator::class.java)

        buttons = arrayOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4)
        )

        statusText = findViewById(R.id.statusText)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (answerIndex == -1) {
                    Toast.makeText(this, "Clique sur 'Jouer !' d'abord", Toast.LENGTH_SHORT).show()
                } else if (index == answerIndex) {
                    Toast.makeText(this, "🎉 Bravo !", Toast.LENGTH_SHORT).show()
                    statusText.text = "✅ Bonne réponse !"
                    answerIndex = -1
                } else {
                    Toast.makeText(this, "❌ Mauvais objet", Toast.LENGTH_SHORT).show()
                    statusText.text = "❌ Mauvais choix"
                }
            }
        }

        findViewById<Button>(R.id.playButton).setOnClickListener {
            playIntroAndStartGame()
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun highlightButton(index: Int, highlight: Boolean) {
        val button = buttons[index]
        button.setBackgroundColor(
            if (highlight)
                ContextCompat.getColor(this, android.R.color.holo_blue_light)
            else
                Color.TRANSPARENT
        )
    }

    private fun playIntroAndStartGame() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "🎬 Présentation des objets..."
            for (i in buttons.indices) {
                highlightButton(i, true)
                vibratePattern(patterns[i])
                delay(1000)
                highlightButton(i, false)
                delay(300)
            }

            for (i in 3 downTo 1) {
                statusText.text = "$i..."
                delay(1000)
            }

            statusText.text = "Écoute bien..."
            delay(1000)

            answerIndex = Random.nextInt(4)
            vibratePattern(patterns[answerIndex])
            statusText.text = "Quel objet était-ce ?"
        }
    }
}
