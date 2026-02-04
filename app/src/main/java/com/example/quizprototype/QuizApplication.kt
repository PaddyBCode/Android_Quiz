package com.example.quizprototype

import android.app.Application
import com.example.quizprototype.data.AppContainer
import com.example.quizprototype.data.DefaultAppContainer

class QuizApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}
