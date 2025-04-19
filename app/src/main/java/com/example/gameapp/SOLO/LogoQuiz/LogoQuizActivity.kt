package com.example.gameapp.SOLO.LogoQuiz

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R

class LogoQuizActivity : AppCompatActivity() {

    private lateinit var logoImageView: ImageView
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var feedbackText: TextView
    private lateinit var scoreText: TextView

    private var currentLogoIndex = 0
    private var score = 0

    // Liste des logos et des réponses attendues
    private val logos = listOf(
        Pair(R.drawable.logo1, "shell"),
        Pair(R.drawable.logo2, "nissan"),
        Pair(R.drawable.logo3, "paypal"),
        Pair(R.drawable.logo4, "la coste")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_quiz)

        // Liens avec le layout
        logoImageView = findViewById(R.id.logoImageView)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        feedbackText = findViewById(R.id.feedbackText)
        scoreText = findViewById(R.id.scoreText)

        // Affiche le premier logo
        showNextLogo()

        submitButton.setOnClickListener {
            val userAnswer = answerInput.text.toString().trim().lowercase()
            val correctAnswer = logos[currentLogoIndex].second

            if (userAnswer == correctAnswer) {
                score++
                feedbackText.text = "✅ Bonne réponse !"
                MediaPlayer.create(this, R.raw.bonne_reponse).start()
            } else {
                feedbackText.text = "❌ Mauvaise réponse : c'était \"$correctAnswer\""
                MediaPlayer.create(this, R.raw.mauvaise_reponse).start()
            }

            scoreText.text = "Score : $score"

            // Passe au logo suivant après un petit délai
            currentLogoIndex++

            if (currentLogoIndex < logos.size) {
                answerInput.setText("")
                logoImageView.postDelayed({ showNextLogo(); feedbackText.text = "" }, 1500)
            } else {
                submitButton.isEnabled = false
                feedbackText.text = "Fin du jeu ! Score final : $score/${logos.size}"
            }
        }
    }

    private fun showNextLogo() {
        logoImageView.setImageResource(logos[currentLogoIndex].first)
    }
}
