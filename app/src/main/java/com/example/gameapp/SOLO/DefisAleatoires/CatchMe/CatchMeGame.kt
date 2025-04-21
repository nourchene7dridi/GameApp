package com.example.gameapp.SOLO.DefisAleatoires.CatchMe

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.gameapp.R
import com.example.gameapp.SOLO.DefisAleatoires.FinDuJeuActivity
import kotlinx.coroutines.*
import kotlin.random.Random

class CatchMeGame : ComponentActivity() {

    private lateinit var vibrator: Vibrator
    private lateinit var buttons: Array<Button>
    private lateinit var statusText: TextView
    private var answerIndex = -1
    private var currentRound = 1
    private val totalRounds = 3

    private val patternsByRound = arrayOf(
        arrayOf( // Manche 1
            longArrayOf(0, 200),
            longArrayOf(0, 400),
            longArrayOf(0, 150, 100, 150),
            longArrayOf(0, 300, 100, 300)
        ),
        arrayOf( // Manche 2
            longArrayOf(0, 100, 100, 200),
            longArrayOf(0, 600),
            longArrayOf(0, 200, 100, 200),
            longArrayOf(0, 400, 150, 400)
        ),
        arrayOf( // Manche 3
            longArrayOf(0, 100, 50, 100, 50, 100),
            longArrayOf(0, 700),
            longArrayOf(0, 100, 100, 100, 100, 100),
            longArrayOf(0, 500, 200, 500)
        )
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

                    if (currentRound < totalRounds) {
                        currentRound++
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            startRound()
                        }
                    } else {
                        statusText.text = "🏁 Jeu terminé !"
                        goToFinDuJeu()
                    }

                    answerIndex = -1
                } else {
                    Toast.makeText(this, "❌ Mauvais téléphone", Toast.LENGTH_SHORT).show()
                    statusText.text = "❌ Mauvais choix"
                }
            }
        }

        // Démarre automatiquement le jeu sans bouton
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            startRound()
        }
    }

    private fun goToFinDuJeu() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            val intent = Intent(this@CatchMeGame, FinDuJeuActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun startRound() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "🎬 Manche $currentRound : Présentation des vibrations des téléphones..."

            buttons.forEach { it.visibility = View.INVISIBLE }

            val patterns = patternsByRound[currentRound - 1]

            for (i in buttons.indices) {
                highlightButton(i, true)
                vibratePattern(patterns[i])
                buttons[i].visibility = View.VISIBLE
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
            statusText.text = "Quel téléphone était-ce ?"
        }
    }
}
