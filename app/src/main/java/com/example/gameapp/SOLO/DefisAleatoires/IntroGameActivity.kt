package com.example.gameapp.SOLO.DefisAleatoires

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R

class IntroGameActivity : AppCompatActivity() {

    private lateinit var gameName: String
    private lateinit var gameRules: String
    private var gameClass: Class<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_game)

        gameName = intent.getStringExtra("gameName") ?: "Jeu"
        gameRules = intent.getStringExtra("gameRules") ?: "Règles non spécifiées"
        gameClass = intent.getSerializableExtra("gameActivity") as? Class<*>

        val titleTextView = findViewById<TextView>(R.id.gameTitle)
        val rulesTextView = findViewById<TextView>(R.id.gameRules)
        val startButton = findViewById<Button>(R.id.startGameButton)

        titleTextView.text = gameName
        rulesTextView.text = gameRules

        startButton.setOnClickListener {
            gameClass?.let {
                val intent = Intent(this, it)
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Après le jeu, on passe à l’écran de fin
        val intent = Intent(this, FinDuJeuActivity::class.java)
        startActivity(intent)
        finish()
    }
}

