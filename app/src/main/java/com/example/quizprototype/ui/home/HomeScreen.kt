package com.example.quizprototype.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.formatPercent

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenStudyModes: () -> Unit,
    onResumeSession: (Long) -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartWeakQuestions: () -> Unit,
    onDismissMessage: () -> Unit
) {
    val dashboard = uiState.dashboard
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    uiState.userProfile?.let { profile ->
                        Text(
                            text = "Welcome ${profile.username}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Driver Theory Study",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Offline study modes, mock exams, bookmarks, and progress tracking.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (uiState.message != null) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(uiState.message, style = MaterialTheme.typography.bodyLarge)
                            OutlinedButton(onClick = onDismissMessage) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            if (dashboard != null) {
                item {
                    DashboardCard(
                        username = dashboard.let { uiState.userProfile?.username },
                        totalQuestions = dashboard.totalQuestions,
                        readinessPercent = dashboard.readinessPercent,
                        completedSessions = dashboard.completedSessions,
                        bookmarkedQuestions = dashboard.bookmarkedQuestions
                    )
                }

                dashboard.activeSession?.let { session ->
                    item {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Resume session", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${session.title} • Question ${session.currentIndex + 1} of ${session.totalQuestions}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Button(onClick = { onResumeSession(session.sessionId) }) {
                                    Text("Resume")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Study modes", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = onOpenStudyModes, modifier = Modifier.fillMaxWidth()) {
                            Text("Open study mode picker")
                        }
                        OutlinedButton(
                            onClick = onStartWeakQuestions,
                            enabled = (dashboard?.weakQuestionsAvailable ?: 0) > 0,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Review weak questions")
                        }
                    }
                }
            }

            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Study library", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = onOpenBookmarks, modifier = Modifier.fillMaxWidth()) {
                            Text("Bookmarks")
                        }
                        OutlinedButton(onClick = onOpenProgress, modifier = Modifier.fillMaxWidth()) {
                            Text("Progress")
                        }
                        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                            Text("Settings")
                        }
                    }
                }
            }

            if (dashboard?.weakestCategories?.isNotEmpty() == true) {
                item {
                    Text("Focus next on", style = MaterialTheme.typography.titleMedium)
                }
                items(dashboard.weakestCategories) { category ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Accuracy ${formatPercent(category.accuracy)} across ${category.attempts} answered questions",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    username: String?,
    totalQuestions: Int,
    readinessPercent: Int,
    completedSessions: Int,
    bookmarkedQuestions: Int
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (username.isNullOrBlank()) "Dashboard" else "Dashboard for $username",
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SummaryMetric("Question bank", totalQuestions.toString())
                SummaryMetric("Readiness", "$readinessPercent%")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SummaryMetric("Sessions", completedSessions.toString())
                SummaryMetric("Bookmarks", bookmarkedQuestions.toString())
            }
        }
    }
}

@Composable
private fun SummaryMetric(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.headlineSmall)
    }
}
