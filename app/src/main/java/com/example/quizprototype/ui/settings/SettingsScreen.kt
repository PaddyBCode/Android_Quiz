package com.example.quizprototype.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onConfirmReset: () -> Unit,
    onDismissError: () -> Unit,
    onBack: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
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
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Reset profile", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Wipe all local progress, bookmarks, and session history.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { showResetConfirmation = true },
                            enabled = !uiState.isResetting
                        ) {
                            Text(if (uiState.isResetting) "Resetting..." else "Reset profile")
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset profile?") },
            text = {
                Text("This will wipe all local progress and send you back to onboarding.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmation = false
                        onConfirmReset()
                    }
                ) {
                    Text("Yes, reset")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
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
