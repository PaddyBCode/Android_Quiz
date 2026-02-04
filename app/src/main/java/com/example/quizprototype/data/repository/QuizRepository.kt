package com.example.quizprototype.data.repository

import com.example.quizprototype.domain.model.Quiz

interface QuizRepository {
    suspend fun seedIfNeeded()
    suspend fun getFirstQuiz(): Quiz?
}
