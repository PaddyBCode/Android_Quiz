package com.example.quizprototype.domain.model

enum class LicenceType {
    CAR
}

enum class StudyMode {
    PRACTICE,
    MOCK_EXAM,
    QUICK_STUDY,
    WEAK_QUESTIONS,
    BOOKMARKED
}

enum class BadgeId {
    ONBOARDED,
    STUDENT,
    FIRST_GEAR,
    SECOND_GEAR,
    THIRD_GEAR,
    FOURTH_GEAR,
    FIFTH_GEAR,
    NCT,
    FOCUSED,
    COMPLETIONIST
}

enum class AppThemeMode {
    DARK,
    LIGHT
}

enum class ProfileAvatarId {
    WOMAN_DOG,
    WOMAN_CAT,
    MAN_DOG,
    MAN_CAT
}

data class UserProfile(
    val id: Int = 1,
    val username: String,
    val createdAtEpochMillis: Long,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val avatarId: ProfileAvatarId = ProfileAvatarId.WOMAN_DOG
)

data class Category(
    val id: String,
    val licenceType: LicenceType,
    val title: String,
    val description: String,
    val questionCount: Int,
    val correctRate: Float? = null
)

data class Topic(
    val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val questionCount: Int
)

data class AnswerOption(
    val id: String,
    val text: String
)

data class Question(
    val id: String,
    val licenceType: LicenceType,
    val categoryId: String,
    val categoryTitle: String,
    val topicId: String,
    val topicTitle: String,
    val prompt: String,
    val explanation: String,
    val sourceReference: String,
    val assetName: String?,
    val isExamEligible: Boolean,
    val options: List<AnswerOption>,
    val correctOptionId: String
)

data class QuestionQuery(
    val licenceType: LicenceType = LicenceType.CAR,
    val categoryIds: Set<String> = emptySet(),
    val topicIds: Set<String> = emptySet(),
    val weakOnly: Boolean = false,
    val bookmarkedOnly: Boolean = false,
    val examEligibleOnly: Boolean = false,
    val limit: Int? = null
)

data class SessionConfig(
    val mode: StudyMode,
    val title: String,
    val query: QuestionQuery,
    val questionLimit: Int? = null,
    val durationLimitSeconds: Int? = null,
    val immediateFeedback: Boolean,
    val allowReviewBeforeSubmit: Boolean
)

data class SessionQuestion(
    val orderIndex: Int,
    val question: Question,
    val selectedOptionId: String?,
    val isBookmarked: Boolean
)

data class AnswerRecord(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val answeredAtEpochMillis: Long,
    val responseTimeMillis: Long
)

data class ActiveStudySession(
    val id: Long,
    val mode: StudyMode,
    val title: String,
    val questions: List<SessionQuestion>,
    val currentIndex: Int,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val durationLimitSeconds: Int?,
    val immediateFeedback: Boolean,
    val allowReviewBeforeSubmit: Boolean
) {
    val isCompleted: Boolean
        get() = completedAtEpochMillis != null

    val totalQuestions: Int
        get() = questions.size

    val currentQuestion: SessionQuestion?
        get() = questions.getOrNull(currentIndex)
}

data class CategoryPerformance(
    val categoryId: String,
    val title: String,
    val correctAnswers: Int,
    val totalAnswers: Int
) {
    val accuracy: Float
        get() = if (totalAnswers == 0) 0f else correctAnswers.toFloat() / totalAnswers.toFloat()
}

data class SessionResult(
    val sessionId: Long,
    val title: String,
    val mode: StudyMode,
    val score: Int,
    val totalQuestions: Int,
    val durationSeconds: Int,
    val passed: Boolean,
    val categoryBreakdown: List<CategoryPerformance>,
    val answerRecords: List<AnswerRecord>
)

data class BookmarkedQuestion(
    val question: Question,
    val bookmarkedAtEpochMillis: Long
)

data class WeakCategory(
    val categoryId: String,
    val title: String,
    val accuracy: Float,
    val attempts: Int
)

data class SessionSummary(
    val sessionId: Long,
    val title: String,
    val mode: StudyMode,
    val currentIndex: Int,
    val totalQuestions: Int,
    val startedAtEpochMillis: Long,
    val durationLimitSeconds: Int?
)

data class DashboardSummary(
    val totalQuestions: Int,
    val completedSessions: Int,
    val bookmarkedQuestions: Int,
    val weakQuestionsAvailable: Int,
    val readinessPercent: Int,
    val averageScorePercent: Int,
    val activeSession: SessionSummary?,
    val lastResult: SessionResult?,
    val weakestCategories: List<WeakCategory>,
    val focusNextCategory: WeakCategory?,
    val strongestCategory: WeakCategory?
)

data class AchievementBadge(
    val id: BadgeId,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val unlockedAtEpochMillis: Long?
)

data class ProgressSnapshot(
    val completedSessions: Int,
    val totalAnsweredQuestions: Int,
    val correctAnswers: Int,
    val averageScorePercent: Int,
    val strongestCategories: List<WeakCategory>,
    val weakestCategories: List<WeakCategory>,
    val recentResults: List<SessionResult>
)
