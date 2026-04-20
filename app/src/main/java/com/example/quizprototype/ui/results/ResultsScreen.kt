package com.example.quizprototype.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.formatDuration
import com.example.quizprototype.ui.formatPercent

@Composable
fun ResultsScreen(
    uiState: ResultsUiState,
    onBookmarkIncorrect: () -> Unit,
    onDismissMessage: () -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("Loading results...", style = MaterialTheme.typography.bodyLarge)
                }
            }

            uiState.errorMessage != null -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text(uiState.errorMessage, style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = onBackHome) {
                        Text("Back home")
                    }
                }
            }

            uiState.result == null -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("No result available.", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = onBackHome) {
                        Text("Back home")
                    }
                }
            }

            else -> {
                val result = uiState.result
                val scorePercent = if (result.totalQuestions == 0) 0 else result.score * 100 / result.totalQuestions
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(result.title, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    if (result.passed) "Pass" else "Fail",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (result.passed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                FuelGaugeScore(scorePercent = scorePercent)
                                Text(
                                    "Score $scorePercent% • ${result.score}/${result.totalQuestions} correct • Duration ${formatDuration(result.durationSeconds)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedButton(
                                        onClick = onBookmarkIncorrect,
                                        enabled = uiState.incorrectQuestionIds.isNotEmpty() && !uiState.isBookmarkingIncorrect,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            if (uiState.isBookmarkingIncorrect) {
                                                "Saving..."
                                            } else {
                                                "Bookmark wrong answers"
                                            }
                                        )
                                    }
                                    Button(onClick = onBackHome, modifier = Modifier.weight(1f)) {
                                        Text("Return to dashboard")
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.message != null) {
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(uiState.message, style = MaterialTheme.typography.bodyLarge)
                                    OutlinedButton(onClick = onDismissMessage) {
                                        Text("Dismiss")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Category breakdown", style = MaterialTheme.typography.titleMedium)
                    }
                    items(result.categoryBreakdown) { category ->
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(category.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${category.correctAnswers}/${category.totalAnswers} correct • ${formatPercent(category.accuracy)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    item {
                        Text("Review questions", style = MaterialTheme.typography.titleMedium)
                    }
                    items(uiState.reviewItems) { review ->
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(review.categoryTitle, style = MaterialTheme.typography.labelLarge)
                                Text(review.prompt, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    if (review.wasCorrect) "Your answer: ${review.selectedAnswer}" else "Your answer: ${review.selectedAnswer}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (!review.wasCorrect) {
                                    Text(
                                        "Correct answer: ${review.correctAnswer}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(review.explanation, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FuelGaugeScore(scorePercent: Int) {
    val clampedPercent = scorePercent.coerceIn(0, 100)
    val trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.18f)
    val fillColor = MaterialTheme.colorScheme.secondary
    val markerColor = MaterialTheme.colorScheme.onPrimaryContainer
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        ) {
            val gaugeTop = size.height * 0.36f
            val gaugeHeight = 26f
            val gaugeWidth = size.width
            val indicatorX = (gaugeWidth * clampedPercent.toFloat() / 100f).coerceIn(0f, gaugeWidth)

            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, gaugeTop),
                size = Size(gaugeWidth, gaugeHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(0f, gaugeTop),
                size = Size((gaugeWidth * clampedPercent.toFloat() / 100f).coerceAtLeast(0f), gaugeHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )

            listOf(25, 50, 75, 100).forEach { marker ->
                val markerX = gaugeWidth * marker.toFloat() / 100f
                drawLine(
                    color = if (marker == 75) Color(0xFF3CCB78) else markerColor,
                    start = Offset(markerX, gaugeTop - 8f),
                    end = Offset(markerX, gaugeTop + gaugeHeight + 8f),
                    strokeWidth = if (marker == 75) 6f else 4f,
                    cap = StrokeCap.Round
                )
            }

            drawLine(
                color = markerColor,
                start = Offset(indicatorX, gaugeTop - 18f),
                end = Offset(indicatorX, gaugeTop + gaugeHeight + 18f),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }
    }
}
