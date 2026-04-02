package com.example.quizprototype.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.formatDuration
import com.example.quizprototype.ui.formatPercent

@Composable
fun ResultsScreen(
    uiState: ResultsUiState,
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
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(result.title, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    if (result.passed) "Session passed" else "Keep practising",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "Score ${result.score}/${result.totalQuestions} • Duration ${formatDuration(result.durationSeconds)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Button(onClick = onBackHome, modifier = Modifier.fillMaxWidth()) {
                                    Text("Back home")
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
