package com.example.quizprototype.ui.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.ui.theme.LaneWhite
import com.example.quizprototype.ui.theme.RoadGreen

data class BadgeVisual(
    val symbol: String,
    val background: Color,
    val accent: Color
)

@Composable
fun AchievementBadgeArtwork(
    badgeId: BadgeId,
    unlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val visual = badgeVisualFor(badgeId)
    val accentColor = if (unlocked) visual.accent else Color(0xFF9F9F9F)
    val foregroundColor = if (unlocked) LaneWhite else Color(0xFF5F5F5F)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = if (unlocked) 1f else 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = if (unlocked) 0.22f else 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = visual.symbol,
                style = MaterialTheme.typography.headlineSmall,
                color = foregroundColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.85f)
            )
        }
    }
}

fun badgeVisualFor(badgeId: BadgeId): BadgeVisual {
    return when (badgeId) {
        BadgeId.ONBOARDED -> BadgeVisual("ID", RoadGreen, Color(0xFF1B8D67))
        BadgeId.STUDENT -> BadgeVisual("ST", Color(0xFF2A93D5), Color(0xFF59C4F5))
        BadgeId.FIRST_GEAR -> BadgeVisual("1", Color(0xFF2D8CFF), Color(0xFF56B6FF))
        BadgeId.SECOND_GEAR -> BadgeVisual("2", Color(0xFF5FA82C), Color(0xFF94D91F))
        BadgeId.THIRD_GEAR -> BadgeVisual("3", Color(0xFFAA6CF6), Color(0xFFD09BFF))
        BadgeId.FOURTH_GEAR -> BadgeVisual("4", Color(0xFFE06A3B), Color(0xFFFFA34D))
        BadgeId.FIFTH_GEAR -> BadgeVisual("5", Color(0xFFB83D2E), Color(0xFFFF6D5F))
        BadgeId.NCT -> BadgeVisual("NCT", Color(0xFF3D4F5A), Color(0xFF8FB4C9))
        BadgeId.FOCUSED -> BadgeVisual("5Q", Color(0xFF7B62F6), Color(0xFFB399FF))
        BadgeId.COMPLETIONIST -> BadgeVisual("ALL", Color(0xFFB28A1E), Color(0xFFFFD75E))
    }
}
