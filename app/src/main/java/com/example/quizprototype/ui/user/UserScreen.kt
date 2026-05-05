package com.example.quizprototype.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.domain.model.DashboardSummary
import com.example.quizprototype.ui.achievements.AchievementBadgeArtwork
import com.example.quizprototype.ui.profile.ProfileAvatarBubble

@Composable
fun UserScreen(uiState: UserUiState) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UserHeader(uiState = uiState)
            }

            uiState.dashboard?.let { dashboard ->
                item {
                    DashboardCard(dashboard = dashboard)
                }
            }

            item {
                Text("Latest achievements", style = MaterialTheme.typography.titleLarge)
            }

            if (uiState.recentUnlockedAchievements.isEmpty()) {
                item {
                    Card {
                        Text(
                            text = "No achievements unlocked yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(uiState.recentUnlockedAchievements, key = { it.id }) { badge ->
                    RecentAchievementRow(badge = badge)
                }
            }
        }
    }
}

@Composable
private fun UserHeader(uiState: UserUiState) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val profile = uiState.userProfile
            if (profile != null) {
                ProfileAvatarBubble(
                    avatarId = profile.avatarId,
                    modifier = Modifier.size(92.dp),
                    borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = profile?.username ?: "User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your study dashboard",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
private fun DashboardCard(dashboard: DashboardSummary) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Dashboard", style = MaterialTheme.typography.titleLarge)
            LinearProgressIndicator(
                progress = { dashboard.readinessPercent / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UserMetric(
                    label = "Readiness",
                    value = "${dashboard.readinessPercent}%",
                    modifier = Modifier.weight(1f)
                )
                UserMetric(
                    label = "Average",
                    value = "${dashboard.averageScorePercent}%",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UserMetric(
                    label = "Sessions",
                    value = dashboard.completedSessions.toString(),
                    modifier = Modifier.weight(1f)
                )
                UserMetric(
                    label = "Bookmarked",
                    value = dashboard.bookmarkedQuestions.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun UserMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun RecentAchievementRow(badge: AchievementBadge) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AchievementBadgeArtwork(
                badgeId = badge.id,
                unlocked = true,
                modifier = Modifier.size(64.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(badge.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f)
                )
            }
        }
    }
}
