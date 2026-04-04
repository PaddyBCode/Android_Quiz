package com.example.quizprototype.ui.session

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.question.QuestionAssetImage
import com.example.quizprototype.ui.formatDuration

@Composable
fun SessionScreen(
    uiState: SessionUiState,
    onBack: () -> Unit,
    onSelectOption: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    onNextQuestion: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onFinishSession: () -> Unit
) {
    val session = uiState.session
    val currentQuestion = session?.currentQuestion

    Scaffold { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("Loading session...", style = MaterialTheme.typography.bodyLarge)
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

            session == null || currentQuestion == null -> {
                Column(modifier = Modifier.padding(innerPadding).padding(24.dp)) {
                    Text("Session not found.", style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            }

            else -> {
                val selectedOptionId = currentQuestion.selectedOptionId
                val correctOptionId = currentQuestion.question.correctOptionId
                val progress = (session.currentIndex + 1).toFloat() / session.totalQuestions.toFloat()
                val feedbackVisible = session.immediateFeedback && selectedOptionId != null

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
                            Text(session.title, style = MaterialTheme.typography.headlineMedium)
                            Text(
                                "Question ${session.currentIndex + 1} of ${session.totalQuestions}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            uiState.remainingSeconds?.let { seconds ->
                                Text(
                                    "Time remaining ${formatDuration(seconds)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        }
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(session.questions) { index, question ->
                                FilterChip(
                                    selected = index == session.currentIndex,
                                    onClick = { onJumpToQuestion(index) },
                                    label = {
                                        val label = if (question.selectedOptionId == null) "${index + 1}" else "${index + 1} •"
                                        Text(label)
                                    }
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
                                Text(currentQuestion.question.categoryTitle, style = MaterialTheme.typography.labelLarge)
                                Text(currentQuestion.question.topicTitle, style = MaterialTheme.typography.labelMedium)
                                QuestionAssetImage(
                                    assetName = currentQuestion.question.assetName,
                                    contentDescription = currentQuestion.question.prompt
                                )
                                Text(currentQuestion.question.prompt, style = MaterialTheme.typography.titleLarge)
                                OutlinedButton(onClick = onToggleBookmark) {
                                    Text(if (currentQuestion.isBookmarked) "Remove bookmark" else "Add bookmark")
                                }
                            }
                        }
                    }

                    itemsIndexed(currentQuestion.question.options) { _, option ->
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = selectedOptionId == option.id,
                                    onClick = { onSelectOption(option.id) }
                                )
                                Column(modifier = Modifier.padding(vertical = 14.dp)) {
                                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                                    if (feedbackVisible && option.id == correctOptionId) {
                                        Text("Correct answer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    if (feedbackVisible) {
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        if (selectedOptionId == correctOptionId) "Correct" else "Review this one",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(currentQuestion.question.explanation, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "Source: ${currentQuestion.question.sourceReference}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        val isLastQuestion = session.currentIndex == session.totalQuestions - 1
                        val canAdvance = selectedOptionId != null || !session.immediateFeedback
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    if (isLastQuestion) {
                                        onFinishSession()
                                    } else {
                                        onNextQuestion()
                                    }
                                },
                                enabled = canAdvance,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isLastQuestion) "Finish session" else "Next question")
                            }
                            if (!isLastQuestion) {
                                OutlinedButton(
                                    onClick = onFinishSession,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("End session now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
