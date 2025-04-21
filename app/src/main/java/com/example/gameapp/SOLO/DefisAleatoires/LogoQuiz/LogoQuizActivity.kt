package com.example.gameapp.SOLO.DefisAleatoires.LogoQuiz

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R
import android.content.Intent

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
        Pair(R.drawable.logo1, "Shell"),
        Pair(R.drawable.logo2, "Nissan"),
        Pair(R.drawable.logo3, "Paypal"),
        Pair(R.drawable.logo4, "La Coste")
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
            val correctAnswer = logos[currentLogoIndex].second.lowercase()

            // Désactiver le champ après soumission
            answerInput.isEnabled = false

            if (userAnswer == correctAnswer) {
                score++
                feedbackText.text = "✅ Bonne réponse !"
                MediaPlayer.create(this, R.raw.bonne_reponse).start()
            } else {
                feedbackText.text = "❌ Mauvaise réponse : c'était \"$correctAnswer\""
                MediaPlayer.create(this, R.raw.mauvaise_reponse).start()
            }

            scoreText.text = "Score : $score"
            currentLogoIndex++

            if (currentLogoIndex < logos.size) {
                logoImageView.postDelayed({
                    showNextLogo()
                    feedbackText.text = ""
                    answerInput.setText("")
                    answerInput.isEnabled = true // ✅ Réactiver ici
                }, 1500)
            } else {
                val intent = Intent(this, LogoQuizScoreActivity::class.java)
                intent.putExtra("FINAL_SCORE", score)
                intent.putExtra("TOTAL_SCORE", logos.size)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun showNextLogo() {
        logoImageView.setImageResource(logos[currentLogoIndex].first)
    }
}
