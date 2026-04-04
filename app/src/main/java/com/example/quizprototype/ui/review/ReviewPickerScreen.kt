package com.example.quizprototype.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.Category
import com.example.quizprototype.ui.theme.DeepGold
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen

@Composable
fun ReviewPickerScreen(
    uiState: ReviewPickerUiState,
    onBack: () -> Unit,
    onOpenAllQuestions: () -> Unit,
    onOpenBookmarkedQuestions: () -> Unit,
    onOpenCategory: (Category) -> Unit
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = RoadGreen)) {
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
                                "Review the Questions",
                                style = MaterialTheme.typography.headlineMedium,
                                color = LaneWhite
                            )
                            Text(
                                "Open a read-through view with the correct answer already highlighted and review notes available on demand.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = LaneWhite.copy(alpha = 0.9f)
                            )
                        }
                        Text(
                            text = "Revision mode",
                            modifier = Modifier.align(Alignment.TopEnd),
                            style = MaterialTheme.typography.labelLarge,
                            color = DeepGold
                        )
                    }
                }
            }

            item {
                ReviewSourceCard(
                    title = "All Questions",
                    description = "Browse the full question bank in review mode.",
                    buttonLabel = "Review all questions",
                    onClick = onOpenAllQuestions
                )
            }
            item {
                ReviewSourceCard(
                    title = "Bookmarked Questions",
                    description = "Review only the questions you have saved.",
                    buttonLabel = "Review bookmarked questions",
                    onClick = onOpenBookmarkedQuestions
                )
            }
            items(uiState.categories, key = { it.id }) { category ->
                ReviewSourceCard(
                    title = category.title,
                    description = category.description,
                    buttonLabel = "Review ${category.questionCount} questions",
                    onClick = { onOpenCategory(category) }
                )
            }
        }
    }
}

@Composable
private fun ReviewSourceCard(
    title: String,
    description: String,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = RoadGreen)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = LaneWhite)
            Text(description, style = MaterialTheme.typography.bodyLarge, color = LaneWhite.copy(alpha = 0.9f))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text(buttonLabel)
            }
        }
    }
}
