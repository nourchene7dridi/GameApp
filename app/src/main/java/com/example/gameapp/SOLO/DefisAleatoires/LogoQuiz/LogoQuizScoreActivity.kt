package com.example.gameapp.SOLO.DefisAleatoires.LogoQuiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R

class LogoQuizScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_quiz_score) // assure-toi que ce fichier existe bien

        val score = intent.getIntExtra("FINAL_SCORE", 0)
        val total = intent.getIntExtra("TOTAL_SCORE", 0)

        val resultText = findViewById<TextView>(R.id.resultTextView)
        val finalScoreText = findViewById<TextView>(R.id.finalScoreText)

        resultText.text = "Fin du jeu !"
        finalScoreText.text = "Score final : $score/$total"
    }
}

