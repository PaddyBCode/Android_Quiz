package com.example.quizprototype.ui.quiz

import com.example.quizprototype.domain.model.Question

data class QuizUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val description: String = "",
    val currentQuestion: Question? = null,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedOptionId: Int? = null,
    val score: Int = 0,
    val isQuizCompleted: Boolean = false,
    val errorMessage: String? = null
)
