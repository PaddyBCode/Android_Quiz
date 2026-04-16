package com.example.quizprototype.data

import android.content.Context
import com.example.quizprototype.data.content.BundledQuestionPackParser
import com.example.quizprototype.data.content.BundledQuestionPackValidator
import com.example.quizprototype.data.local.QuizDatabase
import com.example.quizprototype.data.repository.AndroidAnalyticsLogger
import com.example.quizprototype.data.repository.AchievementsRepository
import com.example.quizprototype.data.repository.AnalyticsLogger
import com.example.quizprototype.data.repository.BookmarkRepository
import com.example.quizprototype.data.repository.ContentImportRepository
import com.example.quizprototype.data.repository.DefaultAchievementsRepository
import com.example.quizprototype.data.repository.DefaultBookmarkRepository
import com.example.quizprototype.data.repository.DefaultContentImportRepository
import com.example.quizprototype.data.repository.DefaultProgressRepository
import com.example.quizprototype.data.repository.DefaultQuestionBankRepository
import com.example.quizprototype.data.repository.DefaultStudySessionRepository
import com.example.quizprototype.data.repository.DefaultUserProfileRepository
import com.example.quizprototype.data.repository.ProgressRepository
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.data.repository.UserProfileRepository

interface AppContainer {
    val analyticsLogger: AnalyticsLogger
    val contentImportRepository: ContentImportRepository
    val questionBankRepository: QuestionBankRepository
    val bookmarkRepository: BookmarkRepository
    val studySessionRepository: StudySessionRepository
    val progressRepository: ProgressRepository
    val userProfileRepository: UserProfileRepository
    val achievementsRepository: AchievementsRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val quizDatabase = QuizDatabase.getDatabase(context)

    override val analyticsLogger: AnalyticsLogger by lazy {
        AndroidAnalyticsLogger()
    }

    override val contentImportRepository: ContentImportRepository by lazy {
        DefaultContentImportRepository(
            assetManager = context.assets,
            contentDao = quizDatabase.contentDao(),
            parser = BundledQuestionPackParser(),
            validator = BundledQuestionPackValidator(
                assetExists = { assetPath ->
                    runCatching {
                        context.assets.open(assetPath).close()
                        true
                    }.getOrDefault(false)
                },
                drawableExists = { resourceName ->
                    context.resources.getIdentifier(resourceName, "drawable", context.packageName) != 0
                }
            ),
            analyticsLogger = analyticsLogger
        )
    }

    override val questionBankRepository: QuestionBankRepository by lazy {
        DefaultQuestionBankRepository(
            questionBankDao = quizDatabase.questionBankDao(),
            bookmarkDao = quizDatabase.bookmarkDao(),
            studySessionDao = quizDatabase.studySessionDao()
        )
    }

    override val bookmarkRepository: BookmarkRepository by lazy {
        DefaultBookmarkRepository(
            bookmarkDao = quizDatabase.bookmarkDao(),
            questionBankRepository = questionBankRepository,
            achievementsRepository = achievementsRepository,
            analyticsLogger = analyticsLogger
        )
    }

    override val studySessionRepository: StudySessionRepository by lazy {
        DefaultStudySessionRepository(
            studySessionDao = quizDatabase.studySessionDao(),
            questionBankRepository = questionBankRepository,
            questionBankDao = quizDatabase.questionBankDao(),
            bookmarkDao = quizDatabase.bookmarkDao(),
            achievementsRepository = achievementsRepository,
            analyticsLogger = analyticsLogger
        )
    }

    override val progressRepository: ProgressRepository by lazy {
        DefaultProgressRepository(
            questionBankDao = quizDatabase.questionBankDao(),
            bookmarkDao = quizDatabase.bookmarkDao(),
            studySessionDao = quizDatabase.studySessionDao(),
            studySessionRepository = studySessionRepository
        )
    }

    override val userProfileRepository: UserProfileRepository by lazy {
        DefaultUserProfileRepository(
            userProfileDao = quizDatabase.userProfileDao(),
            bookmarkDao = quizDatabase.bookmarkDao(),
            studySessionDao = quizDatabase.studySessionDao(),
            achievementDao = quizDatabase.achievementDao(),
            achievementsRepository = achievementsRepository,
            analyticsLogger = analyticsLogger
        )
    }

    override val achievementsRepository: AchievementsRepository by lazy {
        DefaultAchievementsRepository(
            achievementDao = quizDatabase.achievementDao(),
            analyticsLogger = analyticsLogger
        )
    }
}
