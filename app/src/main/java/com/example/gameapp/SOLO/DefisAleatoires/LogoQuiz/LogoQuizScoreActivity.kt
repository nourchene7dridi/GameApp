package com.example.gameapp.SOLO.DefisAleatoires.LogoQuiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R
import android.content.Intent
import com.example.gameapp.SOLO.DefisAleatoires.FinDuJeuActivity
import kotlinx.coroutines.*


class LogoQuizScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_quiz_score)

        val score = intent.getIntExtra("FINAL_SCORE", 0)
        val total = intent.getIntExtra("TOTAL_SCORE", 0)

        val resultText = findViewById<TextView>(R.id.resultTextView)
        val finalScoreText = findViewById<TextView>(R.id.finalScoreText)

        resultText.text = "Fin du jeu !"
        finalScoreText.text = "Score final : $score/$total"

        // --> Redirection automatique vers FinDuJeuActivity après 3 secondes
        GlobalScope.launch {
            delay(3000)
            val intent = Intent(this@LogoQuizScoreActivity, FinDuJeuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}

