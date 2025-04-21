package com.example.gameapp.SOLO.Entrainement.BoatGame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BoatGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On crée et on affiche la vue du jeu
        val gameView = BoatGameView(this)
        setContentView(gameView)
        // On modifie le titre (c'est plus joli comme ça)
        supportActionBar?.title = "Boat Game"

        // On démarre le jeu
        gameView.startGame()
    }
}

