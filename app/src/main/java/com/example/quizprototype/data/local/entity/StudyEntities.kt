package com.example.quizprototype.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.quizprototype.domain.model.AppThemeMode
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.domain.model.LicenceType
import com.example.quizprototype.domain.model.ProfileAvatarId
import com.example.quizprototype.domain.model.StudyMode

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val licenceType: LicenceType,
    val title: String,
    val description: String,
    val sortOrder: Int
)

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class TopicEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val sortOrder: Int
)

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("topicId"), Index("licenceType")]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val licenceType: LicenceType,
    val categoryId: String,
    val topicId: String,
    val prompt: String,
    val explanation: String,
    val sourceReference: String,
    val assetName: String?,
    val isExamEligible: Boolean,
    val sortOrder: Int
)

@Entity(
    tableName = "answer_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class AnswerOptionEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val text: String,
    val isCorrect: Boolean,
    val sortOrder: Int
)

@Entity(tableName = "content_versions")
data class ContentVersionEntity(
    @PrimaryKey val id: Int = 1,
    val version: String,
    val importedAtEpochMillis: Long,
    val questionCount: Int
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String,
    val createdAtEpochMillis: Long,
    val themeMode: AppThemeMode,
    val avatarId: ProfileAvatarId
)

@Entity(tableName = "achievement_unlocks")
data class AchievementUnlockEntity(
    @PrimaryKey val badgeId: BadgeId,
    val unlockedAtEpochMillis: Long
)

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class BookmarkEntity(
    @PrimaryKey val questionId: String,
    val createdAtEpochMillis: Long
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: StudyMode,
    val title: String,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val currentIndex: Int,
    val totalQuestions: Int,
    val durationLimitSeconds: Int?,
    val immediateFeedback: Boolean,
    val allowReviewBeforeSubmit: Boolean
)

@Entity(
    tableName = "study_session_questions",
    primaryKeys = ["sessionId", "questionId"],
    foreignKeys = [
        ForeignKey(
            entity = StudySessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("questionId")]
)
data class StudySessionQuestionEntity(
    val sessionId: Long,
    val questionId: String,
    val orderIndex: Int
)

@Entity(
    tableName = "answer_records",
    primaryKeys = ["sessionId", "questionId"],
    foreignKeys = [
        ForeignKey(
            entity = StudySessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("questionId")]
)
data class AnswerRecordEntity(
    val sessionId: Long,
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val answeredAtEpochMillis: Long,
    val responseTimeMillis: Long
)
