package com.example.quizprototype.ui.review

enum class ReviewScope {
    ALL,
    BOOKMARKED,
    CATEGORY;

    companion object {
        fun fromRoute(value: String): ReviewScope = entries.firstOrNull { it.name == value } ?: ALL
    }
}
