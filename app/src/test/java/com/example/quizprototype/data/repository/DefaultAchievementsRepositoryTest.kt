package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.AchievementDao
import com.example.quizprototype.data.local.entity.AchievementUnlockEntity
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.domain.model.SessionResult
import com.example.quizprototype.domain.model.StudyMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAchievementsRepositoryTest {

    @Test
    fun profileCreated_unlocksOnboardedBadge() = runBlocking {
        val repository = DefaultAchievementsRepository(
            achievementDao = FakeAchievementDao(),
            analyticsLogger = FakeAnalyticsLogger()
        )

        repository.onProfileCreated()

        val unlockedIds = repository.observeBadges()
            .first()
            .filter { it.unlocked }
            .map { it.id }

        assertTrue(unlockedIds.contains(BadgeId.ONBOARDED))
    }

    @Test
    fun perfectMock_unlocksMockProgressionBadges() = runBlocking {
        val repository = DefaultAchievementsRepository(
            achievementDao = FakeAchievementDao(),
            analyticsLogger = FakeAnalyticsLogger()
        )

        repository.onSessionCompleted(
            SessionResult(
                sessionId = 1L,
                title = "Exam Style Mock",
                mode = StudyMode.MOCK_EXAM,
                score = 10,
                totalQuestions = 10,
                durationSeconds = 1200,
                passed = true,
                categoryBreakdown = emptyList(),
                answerRecords = emptyList()
            )
        )

        val unlockedIds = repository.observeBadges()
            .first()
            .filter { it.unlocked }
            .map { it.id }

        assertTrue(unlockedIds.contains(BadgeId.THIRD_GEAR))
        assertTrue(unlockedIds.contains(BadgeId.FOURTH_GEAR))
        assertTrue(unlockedIds.contains(BadgeId.FIFTH_GEAR))
    }

    @Test
    fun unlockingAllCoreBadges_unlocksCompletionist() = runBlocking {
        val repository = DefaultAchievementsRepository(
            achievementDao = FakeAchievementDao(),
            analyticsLogger = FakeAnalyticsLogger()
        )

        BadgeId.entries
            .filterNot { it == BadgeId.COMPLETIONIST }
            .forEach { badgeId -> repository.unlockBadge(badgeId) }

        val unlockedIds = repository.observeBadges()
            .first()
            .filter { it.unlocked }
            .map { it.id }

        assertEquals(BadgeId.entries.toSet(), unlockedIds.toSet())
    }

    private class FakeAchievementDao : AchievementDao {
        private val unlocks = mutableListOf<AchievementUnlockEntity>()
        private val unlockFlow = MutableStateFlow<List<AchievementUnlockEntity>>(emptyList())

        override fun observeUnlocks(): Flow<List<AchievementUnlockEntity>> = unlockFlow

        override suspend fun getUnlockedBadgeIds(): List<BadgeId> = unlocks.map { it.badgeId }

        override suspend fun upsertUnlock(unlock: AchievementUnlockEntity) {
            unlocks.removeAll { it.badgeId == unlock.badgeId }
            unlocks.add(unlock)
            unlockFlow.value = unlocks.toList()
        }

        override suspend fun deleteAllUnlocks() {
            unlocks.clear()
            unlockFlow.value = emptyList()
        }
    }

    private class FakeAnalyticsLogger : AnalyticsLogger {
        override fun logEvent(name: String, attributes: Map<String, String>) = Unit

        override fun logError(name: String, throwable: Throwable, attributes: Map<String, String>) = Unit
    }
}
