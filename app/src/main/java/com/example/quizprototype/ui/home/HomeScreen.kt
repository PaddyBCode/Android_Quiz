package com.example.quizprototype.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.theme.DeepGold
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onResumeSession: (Long) -> Unit,
    onOpenCategoryStudy: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenReviewQuestions: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartQuickStudy: () -> Unit,
    onStartMiniMock: () -> Unit,
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                        dashboard?.let { summary ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = LaneWhite.copy(alpha = 0.08f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Your study snapshot",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = DeepGold
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        SummaryMetric(
                                            label = "Question bank",
                                            value = summary.totalQuestions.toString(),
                                            modifier = Modifier.weight(1f),
                                            labelColor = LaneWhite.copy(alpha = 0.78f),
                                            valueColor = LaneWhite
                                        )
                                        SummaryMetric(
                                            label = "Readiness",
                                            value = "${summary.readinessPercent}%",
                                            modifier = Modifier.weight(1f),
                                            labelColor = LaneWhite.copy(alpha = 0.78f),
                                            valueColor = LaneWhite
                                        )
                                        SummaryMetric(
                                            label = "Sessions",
                                            value = summary.completedSessions.toString(),
                                            modifier = Modifier.weight(1f),
                                            labelColor = LaneWhite.copy(alpha = 0.78f),
                                            valueColor = LaneWhite
                                        )
                                    }
                                }
                            }
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
                        Text("Quiz Selector", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = onStartQuickStudy, modifier = Modifier.fillMaxWidth()) {
                            Text("Quick Test")
                        }
                        OutlinedButton(onClick = onStartMiniMock, modifier = Modifier.fillMaxWidth()) {
                            Text("Mini Mock")
                        }
                        OutlinedButton(onClick = onStartExamStyleMock, modifier = Modifier.fillMaxWidth()) {
                            Text("Exam Mock")
                        }
                        Button(onClick = onOpenCategoryStudy, modifier = Modifier.fillMaxWidth()) {
                            Text("Quiz by Category")
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
                        Text("Study Room", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = onOpenReviewQuestions, modifier = Modifier.fillMaxWidth()) {
                            Text("Review Questions")
                        }
                        OutlinedButton(onClick = onOpenBookmarks, modifier = Modifier.fillMaxWidth()) {
                            Text("Bookmarked Questions")
                        }
                        OutlinedButton(
                            onClick = onStartWeakQuestions,
                            enabled = (dashboard?.weakQuestionsAvailable ?: 0) > 0,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Revise weak questions")
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
                        Text("Profile", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = onOpenAchievements, modifier = Modifier.fillMaxWidth()) {
                            Text("Achievements")
                        }
                        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                            Text("Settings")
                        }
                    }
                }
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
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = labelColor)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = valueColor
        )
    }
}
