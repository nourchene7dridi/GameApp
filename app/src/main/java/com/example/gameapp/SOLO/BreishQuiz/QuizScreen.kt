package com.example.gameapp.SOLO.BreishQuiz

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.gameapp.R

@Composable
fun QuizScreen(questions: List<Question>, onQuizFinished: (score: Int) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val question = questions[currentQuestionIndex]

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Question ${currentQuestionIndex + 1}/${questions.size}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = question.text,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            question.options.forEachIndexed { index, option ->
                Button(
                    onClick = { selectedOption = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption == index) Color(0xFF03DAC5) else Color(0xFF6200EE),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(option)
                }
            }

            if (selectedOption != -1) {
                Button(
                    onClick = {
                        scope.launch {
                            if (selectedOption == question.correctIndex) {
                                score++

                                // Libération du MediaPlayer pour éviter les problèmes
                                val player = MediaPlayer.create(context, R.raw.bonne_reponse)
                                player.setOnCompletionListener {
                                    it.release()
                                }
                                player.start()

                                snackbarHostState.showSnackbar("✅ Bonne réponse ! T'es un vrai breton 🐟")
                            } else {
                                // Libération du MediaPlayer pour éviter les problèmes
                                val player = MediaPlayer.create(context, R.raw.mauvaise_reponse)
                                player.setOnCompletionListener {
                                    it.release()
                                }
                                player.start()

                                snackbarHostState.showSnackbar("❌ Oups ! Va falloir réviser la Breizh attitude 😅")
                            }

                            delay(300)

                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedOption = -1
                            } else {
                                onQuizFinished(score)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Suivant", color = Color.White)
                }
            }
        }

        // Snackbar en bas de l’écran
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
