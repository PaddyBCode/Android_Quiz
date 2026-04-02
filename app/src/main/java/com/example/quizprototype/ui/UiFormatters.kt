package com.example.quizprototype.ui

fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

fun formatPercent(value: Float): String = "${(value * 100).toInt()}%"
