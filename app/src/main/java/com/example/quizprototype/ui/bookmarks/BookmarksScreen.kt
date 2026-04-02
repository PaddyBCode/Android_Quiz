package com.example.quizprototype.ui.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.BookmarkedQuestion

@Composable
fun BookmarksScreen(
    questions: List<BookmarkedQuestion>,
    onBack: () -> Unit,
    onStartReview: () -> Unit
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
                    Text("Bookmarks", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Saved questions stay available offline and can be turned into a review session.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                Button(
                    onClick = onStartReview,
                    enabled = questions.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start bookmarked review")
                }
            }

            if (questions.isEmpty()) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("No bookmarks yet.", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            } else {
                items(questions) { bookmark ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(bookmark.question.categoryTitle, style = MaterialTheme.typography.labelLarge)
                            Text(bookmark.question.prompt, style = MaterialTheme.typography.titleMedium)
                            Text(bookmark.question.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
