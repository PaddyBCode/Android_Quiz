package com.example.quizprototype.ui.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quizprototype.data.repository.AchievementsRepository
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.ui.theme.DeepGold
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen
import kotlinx.coroutines.delay

@Composable
fun AchievementUnlockToastHost(
    achievementsRepository: AchievementsRepository,
    modifier: Modifier = Modifier
) {
    var currentBadge by remember { mutableStateOf<AchievementBadge?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(achievementsRepository) {
        achievementsRepository.observeUnlockEvents().collect { badge ->
            currentBadge = badge
            isVisible = true
            delay(2400)
            isVisible = false
            delay(250)
            currentBadge = null
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible && currentBadge != null,
            enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            currentBadge?.let { badge ->
                AchievementUnlockToast(badge = badge)
            }
        }
    }
}

@Composable
private fun AchievementUnlockToast(badge: AchievementBadge) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RoadGreen)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AchievementBadgeArtwork(
                badgeId = badge.id,
                unlocked = true,
                modifier = Modifier.size(68.dp)
            )
            androidx.compose.foundation.layout.Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Achievement unlocked",
                    style = MaterialTheme.typography.labelLarge,
                    color = DeepGold
                )
                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LaneWhite
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LaneWhite.copy(alpha = 0.9f)
                )
            }
        }
    }
}
