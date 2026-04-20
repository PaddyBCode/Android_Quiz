package com.example.quizprototype.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizprototype.ui.question.QuestionAssetImage
import com.example.quizprototype.ui.formatDuration
import com.example.quizprototype.ui.theme.DeepGold

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
                var showFinishDialog by rememberSaveable { mutableStateOf(false) }
                val selectedOptionId = currentQuestion.selectedOptionId
                val correctOptionId = currentQuestion.question.correctOptionId
                val progress = (session.currentIndex + 1).toFloat() / session.totalQuestions.toFloat()
                val feedbackVisible = session.immediateFeedback && selectedOptionId != null
                val unansweredQuestionNumbers = uiState.unansweredQuestionNumbers

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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(session.title, style = MaterialTheme.typography.headlineMedium)
                                    Text(
                                        "Question ${session.currentIndex + 1} of ${session.totalQuestions}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "Answered ${session.totalQuestions - unansweredQuestionNumbers.size} of ${session.totalQuestions}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (unansweredQuestionNumbers.isEmpty()) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                uiState.remainingSeconds?.let { seconds ->
                                    session.durationLimitSeconds?.let { limit ->
                                        CountdownWheel(
                                            remainingSeconds = seconds,
                                            durationLimitSeconds = limit
                                        )
                                    }
                                }
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
                                Text(
                                    currentQuestion.question.categoryTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = DeepGold
                                )
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
                        val answerLocked = selectedOptionId != null
                        val borderColor = when {
                            selectedOptionId == option.id && option.id == correctOptionId -> MaterialTheme.colorScheme.primary
                            selectedOptionId == option.id && option.id != correctOptionId -> MaterialTheme.colorScheme.error
                            selectedOptionId != null && selectedOptionId != correctOptionId && option.id == correctOptionId -> MaterialTheme.colorScheme.primary
                            else -> Color.Transparent
                        }
                        Card(
                            border = BorderStroke(2.dp, borderColor),
                            modifier = Modifier.selectable(
                                selected = selectedOptionId == option.id,
                                enabled = !answerLocked,
                                role = Role.RadioButton,
                                onClick = { onSelectOption(option.id) }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = selectedOptionId == option.id,
                                    onClick = null
                                )
                                Column(modifier = Modifier.padding(vertical = 14.dp)) {
                                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                                    if (selectedOptionId != null && option.id == correctOptionId) {
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
                                        showFinishDialog = true
                                    } else {
                                        onNextQuestion()
                                    }
                                },
                                enabled = canAdvance,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isLastQuestion) "Submit quiz" else "Next question")
                            }
                            if (!isLastQuestion) {
                                OutlinedButton(
                                    onClick = { showFinishDialog = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Submit quiz")
                                }
                            }
                        }
                    }
                }

                if (showFinishDialog) {
                    FinishSessionDialog(
                        unansweredQuestionNumbers = unansweredQuestionNumbers,
                        onDismiss = { showFinishDialog = false },
                        onConfirm = {
                            showFinishDialog = false
                            onFinishSession()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CountdownWheel(
    remainingSeconds: Int,
    durationLimitSeconds: Int
) {
    val progress = if (durationLimitSeconds == 0) {
        0f
    } else {
        remainingSeconds.toFloat() / durationLimitSeconds.toFloat()
    }.coerceIn(0f, 1f)
    val indicatorColor = when {
        progress <= 0.2f -> MaterialTheme.colorScheme.error
        progress <= 0.4f -> DeepGold
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier.size(88.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(88.dp),
            color = indicatorColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 7.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = formatDuration(remainingSeconds),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "left",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FinishSessionDialog(
    unansweredQuestionNumbers: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Submit quiz?")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (unansweredQuestionNumbers.isEmpty()) {
                    Text("You have answered every question. Do you want to submit your quiz now?")
                } else {
                    Text(
                        text = "You still have ${unansweredQuestionNumbers.size} unanswered ${if (unansweredQuestionNumbers.size == 1) "question" else "questions"}.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Unanswered: ${unansweredQuestionNumbers.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text("Submitting now will end the session and count those questions as incorrect.")
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Submit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Keep studying")
            }
        }
    )
}
