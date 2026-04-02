package com.example.quizprototype.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onBack: () -> Unit
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
                    Text("Settings", style = MaterialTheme.typography.headlineMedium)
                }
            }
            item {
                SettingsCard(
                    title = "Offline-first content",
                    body = "The app ships with a bundled question pack and uses local storage for sessions, bookmarks, and progress."
                )
            }
            item {
                SettingsCard(
                    title = "Session behaviour",
                    body = "Practice mode shows explanations immediately. Mock exam mode is timed and defers review until the end."
                )
            }
            item {
                SettingsCard(
                    title = "Prototype release checklist",
                    body = "Before publishing, replace sample content, validate the import file, run smoke tests, and update store screenshots and metadata."
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, body: String) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
