package com.example.quizprototype.ui.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.ui.theme.DeepGold
import com.example.quizprototype.ui.theme.LaneWhite

@Composable
fun AchievementsScreen(
    uiState: AchievementsUiState,
    onBack: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack) {
                    Text("Back")
                }
                Text("Achievements", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = buildString {
                        uiState.userProfile?.let { append("${it.username} • ") }
                        append("${uiState.unlockedCount}/${uiState.badges.size} unlocked")
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.badges, key = { it.id }) { badge ->
                    AchievementCard(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(badge: AchievementBadge) {
    val visual = badgeVisualFor(badge.id)
    val backgroundColor = if (badge.unlocked) visual.background else Color(0xFFD7D7D7)
    val foregroundColor = if (badge.unlocked) LaneWhite else Color(0xFF5F5F5F)

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AchievementBadgeArtwork(
                badgeId = badge.id,
                unlocked = badge.unlocked,
                modifier = Modifier
                    .size(76.dp)
            )

            Text(
                text = badge.title,
                style = MaterialTheme.typography.titleMedium,
                color = foregroundColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodySmall,
                color = foregroundColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.height(52.dp)
            )
            Text(
                text = if (badge.unlocked) "Unlocked" else "Locked",
                style = MaterialTheme.typography.labelLarge,
                color = if (badge.unlocked) DeepGold else foregroundColor.copy(alpha = 0.8f)
            )
        }
    }
}
