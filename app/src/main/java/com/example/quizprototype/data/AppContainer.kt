package com.example.quizprototype.data

import android.content.Context
import com.example.quizprototype.data.local.QuizDatabase
import com.example.quizprototype.data.repository.DefaultQuizRepository
import com.example.quizprototype.data.repository.QuizRepository

interface AppContainer {
    val quizRepository: QuizRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val quizDatabase = QuizDatabase.getDatabase(context)

    override val quizRepository: QuizRepository by lazy {
        DefaultQuizRepository(quizDatabase.quizDao())
    }
}
