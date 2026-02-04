package com.example.quizprototype.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when {
            uiState.isLoading -> {
                CenteredMessage(
                    modifier = Modifier.padding(innerPadding),
                    message = "Loading quiz..."
                )
            }

            uiState.errorMessage != null -> {
                CenteredMessage(
                    modifier = Modifier.padding(innerPadding),
                    message = uiState.errorMessage
                )
            }

            uiState.isQuizCompleted -> {
                QuizCompleted(
                    modifier = Modifier.padding(innerPadding),
                    score = uiState.score,
                    totalQuestions = uiState.totalQuestions,
                    onRestartQuiz = onRestartQuiz
                )
            }

            else -> {
                QuizQuestionContent(
                    modifier = Modifier.padding(innerPadding),
                    uiState = uiState,
                    onOptionSelected = onOptionSelected,
                    onNextQuestion = onNextQuestion
                )
            }
        }
    }
}

@Composable
private fun QuizQuestionContent(
    modifier: Modifier,
    uiState: QuizUiState,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit
) {
    val question = uiState.currentQuestion ?: return
    val progress = (uiState.questionIndex + 1).toFloat() / uiState.totalQuestions
    val nextButtonText = if (uiState.questionIndex == uiState.totalQuestions - 1) "Finish Quiz" else "Next Question"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = uiState.title,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = uiState.description,
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Question ${uiState.questionIndex + 1} of ${uiState.totalQuestions}",
            style = MaterialTheme.typography.labelLarge
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = question.prompt,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        question.options.forEach { option ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = uiState.selectedOptionId == option.id,
                        onClick = { onOptionSelected(option.id) }
                    )
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
            }
        }

        Button(
            onClick = onNextQuestion,
            enabled = uiState.selectedOptionId != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(nextButtonText)
        }
    }
}

@Composable
private fun QuizCompleted(
    modifier: Modifier,
    score: Int,
    totalQuestions: Int,
    onRestartQuiz: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quiz Completed",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "You scored $score out of $totalQuestions.",
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = onRestartQuiz) {
            Text("Try Again")
        }
    }
}

@Composable
private fun CenteredMessage(
    modifier: Modifier,
    message: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
