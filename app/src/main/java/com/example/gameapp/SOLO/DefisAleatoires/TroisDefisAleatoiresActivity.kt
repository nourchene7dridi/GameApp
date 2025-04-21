package com.example.gameapp.SOLO.DefisAleatoires

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R
import com.example.gameapp.SOLO.Entrainement.BoatGame.BoatGameActivity
import com.example.gameapp.SOLO.Entrainement.BreishQuiz.QuizActivity
import com.example.gameapp.SOLO.Entrainement.CatchMe.CatchMeGame
import com.example.gameapp.SOLO.Entrainement.LogoQuiz.LogoQuizActivity
import com.example.gameapp.SOLO.Entrainement.Pong.PongGameActivity
import com.example.gameapp.SOLO.Entrainement.ShakeIt.ShakeItGame

class TroisDefisAleatoiresActivity : AppCompatActivity() {

    private val allGames = listOf(
        CatchMeGame::class.java,
        QuizActivity::class.java,
        PongGameActivity::class.java,
        ShakeItGame::class.java,
        BoatGameActivity::class.java,
        LogoQuizActivity::class.java
    )

    private lateinit var selectedGames: List<Class<*>>
    private var currentGameIndex = 0
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedGames = allGames.shuffled().take(3)
        currentGameIndex = 0
        launchNextGame()
    }

    private fun launchNextGame() {
        if (currentGameIndex < selectedGames.size) {
            val intent = Intent(this, selectedGames[currentGameIndex])
            currentGameIndex++
            startActivityForResult(intent, 1)
        } else {
            playEndMusic()
        }
    }

    private fun playEndMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.fin)  // ← ton fichier de fin ici
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            finish()  // ← ou affiche un écran "Bravo"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        launchNextGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
