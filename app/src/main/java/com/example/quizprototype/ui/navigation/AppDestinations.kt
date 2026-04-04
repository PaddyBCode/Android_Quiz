package com.example.quizprototype.ui.navigation

object AppDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val STUDY_PICKER = "study_picker"
    const val CATEGORY_STUDY = "category_study"
    const val REVIEW_PICKER = "review_picker"
    const val REVIEW_QUESTIONS = "review_questions"
    const val SESSION = "session"
    const val RESULTS = "results"
    const val PROGRESS = "progress"
    const val BOOKMARKS = "bookmarks"
    const val SETTINGS = "settings"

    const val SESSION_ID_ARG = "sessionId"
    const val REVIEW_SCOPE_ARG = "reviewScope"
    const val REVIEW_FILTER_ID_ARG = "reviewFilterId"

    const val SESSION_ROUTE = "$SESSION/{$SESSION_ID_ARG}"
    const val RESULTS_ROUTE = "$RESULTS/{$SESSION_ID_ARG}"
    const val REVIEW_QUESTIONS_ROUTE = "$REVIEW_QUESTIONS/{$REVIEW_SCOPE_ARG}/{$REVIEW_FILTER_ID_ARG}"

    fun sessionRoute(sessionId: Long): String = "$SESSION/$sessionId"

    fun resultsRoute(sessionId: Long): String = "$RESULTS/$sessionId"

    fun reviewQuestionsRoute(scope: String, filterId: String = "none"): String =
        "$REVIEW_QUESTIONS/$scope/$filterId"
}
