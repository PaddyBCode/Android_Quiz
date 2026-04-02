package com.example.quizprototype.ui.navigation

object AppDestinations {
    const val HOME = "home"
    const val STUDY_PICKER = "study_picker"
    const val SESSION = "session"
    const val RESULTS = "results"
    const val PROGRESS = "progress"
    const val BOOKMARKS = "bookmarks"
    const val SETTINGS = "settings"

    const val SESSION_ID_ARG = "sessionId"

    const val SESSION_ROUTE = "$SESSION/{$SESSION_ID_ARG}"
    const val RESULTS_ROUTE = "$RESULTS/{$SESSION_ID_ARG}"

    fun sessionRoute(sessionId: Long): String = "$SESSION/$sessionId"

    fun resultsRoute(sessionId: Long): String = "$RESULTS/$sessionId"
}
