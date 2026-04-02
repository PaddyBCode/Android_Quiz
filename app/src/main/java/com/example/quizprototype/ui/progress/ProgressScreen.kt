package com.example.quizprototype.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.ProgressSnapshot
import com.example.quizprototype.ui.formatPercent

@Composable
fun ProgressScreen(
    snapshot: ProgressSnapshot,
    onBack: () -> Unit
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                    Text("Progress", style = MaterialTheme.typography.headlineMedium)
                }
            }

            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Snapshot", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ProgressMetric("Sessions", snapshot.completedSessions.toString())
                            ProgressMetric("Answered", snapshot.totalAnsweredQuestions.toString())
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ProgressMetric("Correct", snapshot.correctAnswers.toString())
                            ProgressMetric("Average", "${snapshot.averageScorePercent}%")
                        }
                    }
                }
            }

            if (snapshot.weakestCategories.isNotEmpty()) {
                item { Text("Weakest categories", style = MaterialTheme.typography.titleMedium) }
                items(snapshot.weakestCategories) { category ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Accuracy ${formatPercent(category.accuracy)} • ${category.attempts} answers",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            if (snapshot.strongestCategories.isNotEmpty()) {
                item { Text("Strongest categories", style = MaterialTheme.typography.titleMedium) }
                items(snapshot.strongestCategories) { category ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Accuracy ${formatPercent(category.accuracy)} • ${category.attempts} answers",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            if (snapshot.recentResults.isNotEmpty()) {
                item { Text("Recent results", style = MaterialTheme.typography.titleMedium) }
                items(snapshot.recentResults) { result ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(result.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${result.score}/${result.totalQuestions} • ${if (result.passed) "Passed" else "Review needed"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressMetric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.headlineSmall)
    }
}
