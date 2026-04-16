package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.AchievementDao
import com.example.quizprototype.data.local.entity.AchievementUnlockEntity
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.domain.model.SessionResult
import com.example.quizprototype.domain.model.StudyMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class DefaultAchievementsRepository(
    private val achievementDao: AchievementDao,
    private val analyticsLogger: AnalyticsLogger
) : AchievementsRepository {
    private val unlockEvents = MutableSharedFlow<AchievementBadge>(extraBufferCapacity = 16)

    override fun observeBadges(): Flow<List<AchievementBadge>> {
        return achievementDao.observeUnlocks().map { unlocks ->
            val unlockedById = unlocks.associateBy { it.badgeId }
            BADGE_DEFINITIONS.map { definition ->
                definition.toBadge(unlockedById[definition.id])
            }
        }
    }

    override fun observeUnlockEvents(): Flow<AchievementBadge> = unlockEvents.asSharedFlow()

    override suspend fun unlockBadge(badgeId: BadgeId) {
        val unlockedIds = achievementDao.getUnlockedBadgeIds().toSet()
        if (unlockedIds.contains(badgeId)) return

        val unlockedAt = System.currentTimeMillis()
        achievementDao.upsertUnlock(AchievementUnlockEntity(badgeId = badgeId, unlockedAtEpochMillis = unlockedAt))
        analyticsLogger.logEvent("badge_unlocked", mapOf("badgeId" to badgeId.name))
        BADGE_DEFINITIONS
            .firstOrNull { it.id == badgeId }
            ?.toBadge(AchievementUnlockEntity(badgeId = badgeId, unlockedAtEpochMillis = unlockedAt))
            ?.also(unlockEvents::tryEmit)
        unlockCompletionistIfReady()
    }

    override suspend fun onProfileCreated() {
        unlockBadge(BadgeId.ONBOARDED)
    }

    override suspend fun onCategoryReviewOpened() {
        unlockBadge(BadgeId.STUDENT)
    }

    override suspend fun onSessionCompleted(result: SessionResult) {
        when (result.mode) {
            StudyMode.QUICK_STUDY -> unlockBadge(BadgeId.FIRST_GEAR)
            StudyMode.WEAK_QUESTIONS -> unlockBadge(BadgeId.NCT)
            StudyMode.MOCK_EXAM -> {
                unlockBadge(BadgeId.THIRD_GEAR)
                val scorePercent = if (result.totalQuestions == 0) 0 else result.score * 100 / result.totalQuestions
                if (scorePercent >= 70) {
                    unlockBadge(BadgeId.FOURTH_GEAR)
                }
                if (scorePercent == 100) {
                    unlockBadge(BadgeId.FIFTH_GEAR)
                }
            }

            StudyMode.PRACTICE -> {
                if (result.title.endsWith(" Practice")) {
                    unlockBadge(BadgeId.SECOND_GEAR)
                }
            }

            StudyMode.BOOKMARKED -> Unit
        }
    }

    override suspend fun onBookmarkCountChanged(bookmarkCount: Int) {
        if (bookmarkCount >= 5) {
            unlockBadge(BadgeId.FOCUSED)
        }
    }

    private suspend fun unlockCompletionistIfReady() {
        val unlockedIds = achievementDao.getUnlockedBadgeIds().toSet()
        val coreBadges = BADGE_DEFINITIONS.map { it.id }.filterNot { it == BadgeId.COMPLETIONIST }.toSet()
        if (coreBadges.all(unlockedIds::contains) && !unlockedIds.contains(BadgeId.COMPLETIONIST)) {
            val unlockedAt = System.currentTimeMillis()
            val unlock = AchievementUnlockEntity(
                badgeId = BadgeId.COMPLETIONIST,
                unlockedAtEpochMillis = unlockedAt
            )
            achievementDao.upsertUnlock(unlock)
            analyticsLogger.logEvent("badge_unlocked", mapOf("badgeId" to BadgeId.COMPLETIONIST.name))
            BADGE_DEFINITIONS
                .firstOrNull { it.id == BadgeId.COMPLETIONIST }
                ?.toBadge(unlock)
                ?.also(unlockEvents::tryEmit)
        }
    }

    private data class BadgeDefinition(
        val id: BadgeId,
        val title: String,
        val description: String
    ) {
        fun toBadge(unlock: AchievementUnlockEntity?): AchievementBadge {
            return AchievementBadge(
                id = id,
                title = title,
                description = description,
                unlocked = unlock != null,
                unlockedAtEpochMillis = unlock?.unlockedAtEpochMillis
            )
        }
    }

    companion object {
        private val BADGE_DEFINITIONS = listOf(
            BadgeDefinition(BadgeId.ONBOARDED, "Onboarded", "Create your user profile."),
            BadgeDefinition(BadgeId.STUDENT, "Student", "Review any category of questions."),
            BadgeDefinition(BadgeId.FIRST_GEAR, "1st Gear", "Complete a Quick Quiz."),
            BadgeDefinition(BadgeId.SECOND_GEAR, "2nd Gear", "Complete a Category Quiz."),
            BadgeDefinition(BadgeId.THIRD_GEAR, "3rd Gear", "Complete a Mock Style Quiz."),
            BadgeDefinition(BadgeId.FOURTH_GEAR, "4th Gear", "Score higher than 70 in a mock quiz."),
            BadgeDefinition(BadgeId.FIFTH_GEAR, "5th Gear", "Score 100 in a mock quiz."),
            BadgeDefinition(BadgeId.NCT, "NCT", "Review weak questions."),
            BadgeDefinition(BadgeId.FOCUSED, "Focused", "Bookmark 5 questions."),
            BadgeDefinition(BadgeId.COMPLETIONIST, "Completionist", "Unlock all other badges.")
        )
    }
}
