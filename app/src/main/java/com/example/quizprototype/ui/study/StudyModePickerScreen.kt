package com.example.quizprototype.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudyModePickerScreen(
    uiState: StudyModePickerUiState,
    onBack: () -> Unit,
    onToggleCategory: (String) -> Unit,
    onClearSelection: () -> Unit,
    onStartPractice: () -> Unit,
    onStartQuickStudy: () -> Unit,
    onStartMockExam: () -> Unit,
    onDismissError: () -> Unit
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
                    Text("Choose your study mode", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Use category filters to focus practice or run a mixed mock exam.",
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Category filters", style = MaterialTheme.typography.titleMedium)
                        uiState.categories.forEach { category ->
                            FilterChip(
                                selected = uiState.selectedCategoryIds.contains(category.id),
                                onClick = { onToggleCategory(category.id) },
                                label = { Text("${category.title} (${category.questionCount})") }
                            )
                        }
                        if (uiState.selectedCategoryIds.isNotEmpty()) {
                            OutlinedButton(onClick = onClearSelection) {
                                Text("Clear filters")
                            }
                        }
                    }
                }
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
                    title = "Mock exam",
                    description = "Timed exam-style session with results and review at the end.",
                    buttonLabel = "Start mock exam",
                    enabled = !uiState.isStarting,
                    onClick = onStartMockExam
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
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
                Text(buttonLabel)
            }
        }
    }
}
