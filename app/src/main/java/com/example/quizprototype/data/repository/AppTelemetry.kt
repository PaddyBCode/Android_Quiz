package com.example.quizprototype.data.repository

import android.util.Log

interface AnalyticsLogger {
    fun logEvent(name: String, attributes: Map<String, String> = emptyMap())
    fun logError(name: String, throwable: Throwable, attributes: Map<String, String> = emptyMap())
}

class AndroidAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, attributes: Map<String, String>) {
        Log.d("QuizPrototype", "$name ${attributes.entries.joinToString()}")
    }

    override fun logError(name: String, throwable: Throwable, attributes: Map<String, String>) {
        Log.e("QuizPrototype", "$name ${attributes.entries.joinToString()}", throwable)
    }
}
