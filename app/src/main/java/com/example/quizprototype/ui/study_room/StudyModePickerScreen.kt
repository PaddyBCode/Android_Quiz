package com.example.quizprototype.ui.study_room

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudyModePickerScreen(
    uiState: StudyModePickerUiState,
    onBack: () -> Unit,
    onOpenStudyByCategory: () -> Unit,
    onOpenReviewQuestions: () -> Unit,
    onStartPractice: () -> Unit,
    onStartQuickStudy: () -> Unit,
    onStartMiniMock: () -> Unit,
    onStartExamStyleMock: () -> Unit,
    onDismissError: () -> Unit
) {
    Scaffold { innerPadding ->
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onBack) {
                                Text("Back")
                            }
                            Text(
                                "Choose your study mode",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Pick a study route, focus on one category, or review every question with answers visible.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                            Text(
                                "Built for road-sign clarity and quick revision.",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = "Study routes",
                            modifier = Modifier.align(Alignment.TopEnd),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(uiState.errorMessage, style = MaterialTheme.typography.bodyLarge)
                            OutlinedButton(onClick = onDismissError) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            item {
                StudyModeCard(
                    title = "Study by Category",
                    description = "Choose one category and run a focused practice session.",
                    buttonLabel = "Choose category",
                    enabled = !uiState.isStarting,
                    onClick = onOpenStudyByCategory
                )
            }
            item {
                StudyModeCard(
                    title = "Review the Questions",
                    description = "Browse all questions, bookmarked questions, or a single category with answers already shown.",
                    buttonLabel = "Open review mode",
                    enabled = !uiState.isStarting,
                    onClick = onOpenReviewQuestions
                )
            }
            item {
                StudyModeCard(
                    title = "Practice",
                    description = "Untimed study with explanations after each answer.",
                    buttonLabel = "Start practice",
                    enabled = !uiState.isStarting,
                    onClick = onStartPractice
                )
            }
            item {
                StudyModeCard(
                    title = "Quick study",
                    description = "A short 5-question mixed session for fast revision.",
                    buttonLabel = "Start quick study",
                    enabled = !uiState.isStarting,
                    onClick = onStartQuickStudy
                )
            }
            item {
                StudyModeCard(
                    title = "Mini Mock",
                    description = "Timed exam-style session with results and review at the end.",
                    buttonLabel = "Start mini mock",
                    enabled = !uiState.isStarting,
                    onClick = onStartMiniMock
                )
            }
            item {
                StudyModeCard(
                    title = "Exam Style Mock",
                    description = "A 45-minute timed mock with 30 questions to simulate a longer exam run.",
                    buttonLabel = "Start exam style mock",
                    enabled = !uiState.isStarting,
                    onClick = onStartExamStyleMock
                )
            }
        }
    }
}

@Composable
private fun StudyModeCard(
    title: String,
    description: String,
    buttonLabel: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
            Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
                Text(buttonLabel)
            }
        }
    }
}
