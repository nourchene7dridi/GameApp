package com.example.gameapp.SOLO.BreishQuiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreScreen(
    score: Int,
    total: Int,
    onReplay: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Fin du quiz !", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ton score : $score / $total", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(32.dp))

        // Messages de score
        when {
            score == total -> {
                Text("Champion(ne) des crêpes ! 🥞", fontSize = 20.sp, color = Color(0xFF388E3C))
            }
            score >= total / 2 -> {
                Text("Pas mal, petit marin ⚓", fontSize = 20.sp, color = Color(0xFF1976D2))
            }
            else -> {
                Text(
                    "Tu reviendras quand il pleuvra 🌧️",
                    fontSize = 20.sp,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Bouton Rejouer
        Button(
            onClick = onReplay,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Rejouer", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bouton Retour
        Button(
            onClick = onReturnToMenu,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Retour au menu", color = Color.White)
        }
    }
}
