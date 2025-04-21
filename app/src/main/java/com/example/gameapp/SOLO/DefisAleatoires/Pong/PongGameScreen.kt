package com.example.gameapp.SOLO.DefisAleatoires.Pong

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameapp.R
import kotlinx.coroutines.delay
import android.content.Intent
import androidx.activity.ComponentActivity
import com.example.gameapp.SOLO.DefisAleatoires.FinDuJeuActivity



// ─── VIBRATION ────────────────────────────────────────────────────────────────
fun vibrate(context: Context, duration: Long = 50) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
            .defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}

// ─── SON DE FAIL ───────────────────────────────────────────────────────────────
fun playBadSound(context: Context) {
    val mp = MediaPlayer.create(context, R.raw.mauvaise_reponse)
    mp.setOnCompletionListener { it.release() }
    mp.start()
}

/**
 * @param onReplay        appelée ➞ on remet le jeu à zéro
 * @param onReturnToMenu  appelée ➞ on ferme l'activité
 */
@Composable
fun PongGameScreen(
    onReplay: () -> Unit,
    onReturnToMenu: () -> Unit
) {
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ─── Dimensions dynamiques ─────────────────────────
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val ballRadius = (w * .05f) / 2
        val paddleW = w * .15f
        val paddleH = h * .02f
        val paddleY = h * .9f

        // ─── États ──────────────────────────────────────────
        var ballX by remember { mutableStateOf(w/2) }
        var ballY by remember { mutableStateOf(h/2) }
        var dx by remember { mutableStateOf(w*.007f) }
        var dy by remember { mutableStateOf(h*.007f) }
        var paddleX by remember { mutableStateOf((w-paddleW)/2) }
        var score by remember { mutableStateOf(0) }
        var lives by remember { mutableStateOf(3) }
        var gameOver by remember { mutableStateOf(false) }

        // ─── Boucle de jeu ──────────────────────────────────

        // Lancer FinDuJeuActivity 3 secondes après le Game Over
        LaunchedEffect(gameOver) {
            if (gameOver) {
                delay(3000L)
                context.startActivity(Intent(context, FinDuJeuActivity::class.java))
                if (context is ComponentActivity) {
                    context.finish()
                }
            }
        }

        // ─── Rendu ──────────────────────────────────────────
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    paddleX = (change.position.x - paddleW/2).coerceIn(0f, w-paddleW)
                }
            }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                if(!gameOver) {
                    drawCircle(Color.White, ballRadius, center=Offset(ballX,ballY))
                }
                drawRect(
                    color = if(!gameOver) Color.Green else Color.DarkGray,
                    topLeft = Offset(paddleX,paddleY),
                    size = Size(paddleW,paddleH)
                )
            }
            // Score & vies
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top=32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score: $score", style=TextStyle(Color.White, fontSize=24.sp))
                Text("Vies:  $lives", style=TextStyle(Color.White, fontSize=20.sp))
            }
            // Game Over + boutons
            if(gameOver) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment=Alignment.CenterHorizontally
                ) {
                    Text("Game Over",
                        style=TextStyle(Color.Red,fontSize=36.sp, fontWeight=FontWeight.Bold))
                    Spacer(Modifier.height(16.dp))
                    Text("Score final: $score",
                        style=TextStyle(Color.White,fontSize=24.sp))
                }
            }

        }
    }
}
