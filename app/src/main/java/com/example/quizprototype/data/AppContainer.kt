package com.example.quizprototype.data

import android.content.Context
import com.example.quizprototype.data.content.BundledQuestionPackParser
import com.example.quizprototype.data.local.QuizDatabase
import com.example.quizprototype.data.repository.AndroidAnalyticsLogger
import com.example.quizprototype.data.repository.AnalyticsLogger
import com.example.quizprototype.data.repository.BookmarkRepository
import com.example.quizprototype.data.repository.ContentImportRepository
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
            analyticsLogger = analyticsLogger
        )
    }

    override val studySessionRepository: StudySessionRepository by lazy {
        DefaultStudySessionRepository(
            studySessionDao = quizDatabase.studySessionDao(),
            questionBankRepository = questionBankRepository,
            questionBankDao = quizDatabase.questionBankDao(),
            bookmarkDao = quizDatabase.bookmarkDao(),
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
            analyticsLogger = analyticsLogger
        )
    }
}
