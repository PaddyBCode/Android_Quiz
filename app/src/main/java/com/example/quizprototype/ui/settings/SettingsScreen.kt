package com.example.quizprototype.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.AppThemeMode
import com.example.quizprototype.domain.model.ProfileAvatarId
import com.example.quizprototype.ui.profile.ProfileAvatarBubble
import com.example.quizprototype.ui.profile.ProfileAvatarPicker
import com.example.quizprototype.ui.profile.profileAvatarLabel

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onProfileAvatarSelected: (ProfileAvatarId) -> Unit,
    onConfirmReset: () -> Unit,
    onDismissError: () -> Unit,
    onBack: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var pendingAvatarId by remember(uiState.avatarId, showAvatarPicker) { mutableStateOf(uiState.avatarId) }

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
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Profile picture", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileAvatarBubble(
                                avatarId = uiState.avatarId,
                                modifier = Modifier.size(84.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    profileAvatarLabel(uiState.avatarId),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                OutlinedButton(
                                    onClick = {
                                        pendingAvatarId = uiState.avatarId
                                        showAvatarPicker = true
                                    },
                                    enabled = !uiState.isUpdatingAvatar
                                ) {
                                    Text(if (uiState.isUpdatingAvatar) "Updating..." else "Change profile picture")
                                }
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
                        Text("Appearance", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Choose the app colour mode for study sessions and menus.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ThemeModeButton(
                                label = "Dark",
                                selected = uiState.themeMode == AppThemeMode.DARK,
                                enabled = !uiState.isUpdatingTheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onThemeModeSelected(AppThemeMode.DARK) }
                            )
                            ThemeModeButton(
                                label = "Light",
                                selected = uiState.themeMode == AppThemeMode.LIGHT,
                                enabled = !uiState.isUpdatingTheme,
                                modifier = Modifier.weight(1f),
                                onClick = { onThemeModeSelected(AppThemeMode.LIGHT) }
                            )
                        }
                    }
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

    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            title = { Text("Choose profile picture") },
            text = {
                ProfileAvatarPicker(
                    selectedAvatarId = pendingAvatarId,
                    enabled = !uiState.isUpdatingAvatar,
                    onAvatarSelected = { pendingAvatarId = it },
                    showLabels = false,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAvatarPicker = false
                        onProfileAvatarSelected(pendingAvatarId)
                    },
                    enabled = !uiState.isUpdatingAvatar
                ) {
                    Text(if (uiState.isUpdatingAvatar) "Updating..." else "Update")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAvatarPicker = false }) {
                    Text("Cancel")
                }
            }
        )
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

@Composable
private fun ThemeModeButton(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) {
            Text(label)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) {
            Text(label)
        }
    }
}
