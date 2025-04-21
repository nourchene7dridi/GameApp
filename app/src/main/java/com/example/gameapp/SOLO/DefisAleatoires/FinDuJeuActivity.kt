package com.example.gameapp.SOLO.DefisAleatoires

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gameapp.R

class FinDuJeuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_du_jeu)

        val nextButton = findViewById<Button>(R.id.nextGameButton)
        nextButton.setOnClickListener {
            finish() // Revenir à TroisDefisAleatoiresActivity → onActivityResult()
        }
    }
}

