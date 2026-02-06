package com.example.quizprototype.domain.model

data class Quiz(
    val id: Int,
    val title: String,
    val description: String,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    val prompt: String,
    val category: QuestionCategory,
    val options: List<AnswerOption>,
    val correctOptionId: Int
)

data class AnswerOption(
    val id: Int,
    val text: String
)
