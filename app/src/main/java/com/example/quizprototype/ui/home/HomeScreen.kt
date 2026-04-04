package com.example.quizprototype.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.formatPercent
import com.example.quizprototype.ui.theme.DeepGold
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenStudyModes: () -> Unit,
    onResumeSession: (Long) -> Unit,
    onOpenReviewQuestions: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartQuickStudy: () -> Unit,
    onStartExamStyleMock: () -> Unit,
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = RoadGreen)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            uiState.userProfile?.let { profile ->
                                Text(
                                    text = "Welcome ${profile.username}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DeepGold
                                )
                            }
                            Text(
                                text = "Driver's Theory Study Guide",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DeepGold
                            )
                            Text(
                                text = "Multiple Testing Modes, Mock Exams, Bookmarked Questions, and Progress Tracking",
                                style = MaterialTheme.typography.bodyLarge,
                                color = LaneWhite.copy(alpha = 0.9f)
                            )
                        }
                    }
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
                        focusNext = dashboard.focusNextCategory?.title ?: "Build history",
                        completedSessions = dashboard.completedSessions,
                        bookmarkedQuestions = dashboard.bookmarkedQuestions,
                        strength = dashboard.strongestCategory?.title ?: "Build history"
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
                        OutlinedButton(onClick = onStartQuickStudy, modifier = Modifier.fillMaxWidth()) {
                            Text("Quick Study")
                        }
                        OutlinedButton(onClick = onStartExamStyleMock, modifier = Modifier.fillMaxWidth()) {
                            Text("Exam Style Mock")
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
                        OutlinedButton(onClick = onOpenReviewQuestions, modifier = Modifier.fillMaxWidth()) {
                            Text("Review the Questions")
                        }
                        OutlinedButton(onClick = onOpenBookmarks, modifier = Modifier.fillMaxWidth()) {
                            Text("Bookmarks")
                        }
                        OutlinedButton(onClick = onOpenProgress, modifier = Modifier.fillMaxWidth()) {
                            Text("Progress")
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
    bookmarkedQuestions: Int,
    focusNext: String,
    strength: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RoadGreen)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (username.isNullOrBlank()) "Dashboard" else "Dashboard for $username",
                style = MaterialTheme.typography.titleMedium,
                color = LaneWhite
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SummaryMetric(
                    label = "Question bank",
                    value = totalQuestions.toString(),
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite
                )
                SummaryMetric(
                    label = "Readiness",
                    value = "$readinessPercent%",
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite
                )
                SummaryMetric(
                    label = "Sessions",
                    value = completedSessions.toString(),
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SummaryMetric(
                    label = "Bookmarks",
                    value = bookmarkedQuestions.toString(),
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite
                )
                SummaryMetric(
                    label = "Focus on Next",
                    value = focusNext,
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite,
                    compactValue = true
                )
                SummaryMetric(
                    label = "Strength",
                    value = strength,
                    modifier = Modifier.weight(1f),
                    labelColor = DeepGold,
                    valueColor = LaneWhite,
                    compactValue = true
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.84f),
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    compactValue: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = labelColor)
        Text(
            text = value,
            style = if (compactValue) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
            color = valueColor,
            maxLines = if (compactValue) 2 else 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (compactValue) Modifier.height(48.dp) else Modifier
        )
    }
}
