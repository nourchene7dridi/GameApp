package com.example.gameapp.SOLO.NopeQuiz


data class Question(
    val text: String, // Texte de la question
    val options: List<String>, // Liste des options de réponse
    val correctIndex: Int // L'index de la bonne réponse dans options
)
