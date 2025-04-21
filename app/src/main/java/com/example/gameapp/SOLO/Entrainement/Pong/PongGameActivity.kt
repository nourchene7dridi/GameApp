package com.example.gameapp.SOLO.Entrainement.Pong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gameapp.ui.theme.GameAppTheme

class PongGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameAppTheme {
                PongGameScreen(
                    // Quand on clique sur "Rejouer", on recrée l'activité
                    onReplay = {
                        recreate()
                    },
                    // Quand on clique sur "Retour", on ferme l'activité
                    onReturnToMenu = {
                        finish()
                    }
                )
            }
        }
    }
}
