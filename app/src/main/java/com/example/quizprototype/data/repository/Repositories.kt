package com.example.quizprototype.data.repository

import com.example.quizprototype.domain.model.ActiveStudySession
import com.example.quizprototype.domain.model.BookmarkedQuestion
import com.example.quizprototype.domain.model.Category
import com.example.quizprototype.domain.model.DashboardSummary
import com.example.quizprototype.domain.model.ProgressSnapshot
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.QuestionQuery
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.SessionResult
import com.example.quizprototype.domain.model.SessionSummary
import com.example.quizprototype.domain.model.Topic
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ContentImportRepository {
    suspend fun ensureBundledContent()
}

interface QuestionBankRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun getTopicsForCategory(categoryId: String): List<Topic>
    suspend fun getQuestions(query: QuestionQuery): List<Question>
    suspend fun getQuestionsByIds(questionIds: List<String>): List<Question>
    suspend fun getQuestion(questionId: String): Question?
    suspend fun getQuestionCount(): Int
}

interface BookmarkRepository {
    fun observeBookmarkedQuestions(): Flow<List<BookmarkedQuestion>>
    fun observeBookmarkCount(): Flow<Int>
    fun observeIsBookmarked(questionId: String): Flow<Boolean>
    suspend fun getBookmarkedQuestionIds(): Set<String>
    suspend fun toggleBookmark(questionId: String)
}

interface StudySessionRepository {
    fun observeActiveSessionSummary(): Flow<SessionSummary?>
    fun observeSession(sessionId: Long): Flow<ActiveStudySession?>
    suspend fun getActiveSessionId(): Long?
    suspend fun startSession(config: SessionConfig): Long
    suspend fun submitAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String,
        responseTimeMillis: Long
    )
    suspend fun updateCurrentIndex(sessionId: Long, currentIndex: Int)
    suspend fun completeSession(sessionId: Long): SessionResult
    suspend fun getResult(sessionId: Long): SessionResult?
    suspend fun getWeakQuestionIds(): Set<String>
}

interface ProgressRepository {
    fun observeDashboardSummary(): Flow<DashboardSummary>
    fun observeProgressSnapshot(): Flow<ProgressSnapshot>
}

interface UserProfileRepository {
    fun observeUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfile(): UserProfile?
    suspend fun createProfile(username: String)
}
