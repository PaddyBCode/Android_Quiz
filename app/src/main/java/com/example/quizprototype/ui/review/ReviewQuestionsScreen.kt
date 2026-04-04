package com.example.quizprototype.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.question.QuestionAssetImage

@Composable
fun ReviewQuestionsScreen(
    uiState: ReviewQuestionsUiState,
    onBack: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onPreviousQuestion: () -> Unit,
    onNextQuestion: () -> Unit,
    onToggleNotes: () -> Unit
) {
    val currentQuestion = uiState.currentQuestion

    Scaffold { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("Loading review questions...", style = MaterialTheme.typography.bodyLarge)
                }
            }

            uiState.errorMessage != null -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text(uiState.errorMessage, style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            }

            currentQuestion == null -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("No review question found.", style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onBack) {
                                Text("Back")
                            }
                            Text(uiState.title, style = MaterialTheme.typography.headlineMedium)
                            Text(
                                "Question ${uiState.currentIndex + 1} of ${uiState.questions.size}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            OutlinedButton(onClick = onToggleNotes) {
                                Text(if (uiState.showNotes) "Hide review notes" else "Show review notes")
                            }
                        }
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(uiState.questions) { index, _ ->
                                FilterChip(
                                    selected = index == uiState.currentIndex,
                                    onClick = { onJumpToQuestion(index) },
                                    label = { Text("${index + 1}") }
                                )
                            }
                        }
                    }

                    item {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(currentQuestion.categoryTitle, style = MaterialTheme.typography.labelLarge)
                                Text(currentQuestion.topicTitle, style = MaterialTheme.typography.labelMedium)
                                QuestionAssetImage(
                                    assetName = currentQuestion.assetName,
                                    contentDescription = currentQuestion.prompt
                                )
                                Text(currentQuestion.prompt, style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }

                    itemsIndexed(currentQuestion.options) { _, option ->
                        val isCorrect = option.id == currentQuestion.correctOptionId
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = isCorrect,
                                    onClick = null
                                )
                                Column(modifier = Modifier.padding(vertical = 14.dp)) {
                                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                                    if (isCorrect) {
                                        Text(
                                            "Correct answer",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.showNotes) {
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Review notes", style = MaterialTheme.typography.titleMedium)
                                    Text(currentQuestion.explanation, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "Source: ${currentQuestion.sourceReference}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onPreviousQuestion,
                                enabled = uiState.currentIndex > 0,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Previous")
                            }
                            Button(
                                onClick = onNextQuestion,
                                enabled = uiState.currentIndex < uiState.questions.lastIndex,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Next")
                            }
                        }
                    }
                }
            }
        }
    }
}
