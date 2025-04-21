package com.example.gameapp.SOLO.DefisAleatoires.BreishQuiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gameapp.SOLO.GameListActivity
import com.example.gameapp.ui.theme.GameAppTheme
import com.example.gameapp.SOLO.DefisAleatoires.FinDuJeuActivity
import kotlinx.coroutines.*



class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GameAppTheme {
                var isQuizFinished by remember { mutableStateOf(false) }
                var finalScore by remember { mutableStateOf(0) }

                // On relance le quiz (regénération des questions)
                var questions by remember { mutableStateOf(BreizhQuestions().breizhQuestions) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isQuizFinished) {
                        // Faut lancer FinDuJeuActivity automatiquement après 3 secondes
                        LaunchedEffect(Unit) {
                            delay(3000)
                            val intent = Intent(this@QuizActivity, FinDuJeuActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        ScoreScreen(
                            score = finalScore,
                            total = questions.size,
                            onReplay = {}, //  inutile maintenant
                            onReturnToMenu = {} //  inutile aussi
                        )
                    }


                 else {
                        QuizScreen(
                            questions = questions,
                            onQuizFinished = { score ->
                                finalScore = score
                                isQuizFinished = true
                            }
                        )
                    }
                }
            }
        }
    }
}
