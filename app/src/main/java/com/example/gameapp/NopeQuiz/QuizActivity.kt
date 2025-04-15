package com.example.gameapp.NopeQuiz

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gameapp.GameListActivity
import com.example.gameapp.ui.theme.GameAppTheme

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
                        ScoreScreen(
                            score = finalScore,
                            total = questions.size,
                            onReplay = {
                                // On remet le score à 0
                                finalScore = 0
                                questions = BreizhQuestions().breizhQuestions
                                isQuizFinished = false
                            },
                            onReturnToMenu = {
                                val intent = Intent(this@QuizActivity, GameListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
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
