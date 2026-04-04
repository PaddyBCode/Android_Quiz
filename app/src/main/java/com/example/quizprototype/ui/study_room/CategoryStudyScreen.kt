package com.example.quizprototype.ui.study_room

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
fun CategoryStudyScreen(
    uiState: CategoryStudyUiState,
    onBack: () -> Unit,
    onStartCategory: (Category) -> Unit,
    onDismissError: () -> Unit
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
                                "Study by Category",
                                style = MaterialTheme.typography.headlineMedium,
                                color = LaneWhite
                            )
                            Text(
                                "Pick one category and work through a focused practice session with explanations.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = LaneWhite.copy(alpha = 0.9f)
                            )
                        }
                        Text(
                            text = "Focused practice",
                            modifier = Modifier.align(Alignment.TopEnd),
                            style = MaterialTheme.typography.labelLarge,
                            color = DeepGold
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(uiState.errorMessage, style = MaterialTheme.typography.bodyLarge)
                            OutlinedButton(onClick = onDismissError) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            items(uiState.categories, key = { it.id }) { category ->
                Card(colors = CardDefaults.cardColors(containerColor = RoadGreen)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(category.title, style = MaterialTheme.typography.titleMedium, color = LaneWhite)
                        Text(category.description, style = MaterialTheme.typography.bodyMedium, color = LaneWhite.copy(alpha = 0.9f))
                        Text(
                            "${category.questionCount} questions",
                            style = MaterialTheme.typography.labelLarge,
                            color = DeepGold
                        )
                        Button(
                            onClick = { onStartCategory(category) },
                            enabled = !uiState.isStarting,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start category study")
                        }
                    }
                }
            }
        }
    }
}
