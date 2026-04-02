package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.BookmarkDao
import com.example.quizprototype.data.local.entity.BookmarkEntity
import com.example.quizprototype.domain.model.BookmarkedQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultBookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val questionBankRepository: QuestionBankRepository,
    private val analyticsLogger: AnalyticsLogger
) : BookmarkRepository {

    override fun observeBookmarkedQuestions(): Flow<List<BookmarkedQuestion>> {
        return bookmarkDao.observeBookmarks().map { bookmarks ->
            val questions = questionBankRepository.getQuestionsByIds(bookmarks.map { it.questionId })
                .associateBy { it.id }
            bookmarks.mapNotNull { bookmark ->
                val question = questions[bookmark.questionId] ?: return@mapNotNull null
                BookmarkedQuestion(
                    question = question,
                    bookmarkedAtEpochMillis = bookmark.createdAtEpochMillis
                )
            }
        }
    }

    override fun observeBookmarkCount(): Flow<Int> = bookmarkDao.observeBookmarkCount()

    override fun observeIsBookmarked(questionId: String): Flow<Boolean> = bookmarkDao.observeIsBookmarked(questionId)

    override suspend fun getBookmarkedQuestionIds(): Set<String> = bookmarkDao.getBookmarkedQuestionIds().toSet()

    override suspend fun toggleBookmark(questionId: String) {
        val bookmarkedIds = bookmarkDao.getBookmarkedQuestionIds().toSet()
        if (bookmarkedIds.contains(questionId)) {
            bookmarkDao.deleteBookmark(questionId)
            analyticsLogger.logEvent("bookmark_removed", mapOf("questionId" to questionId))
        } else {
            bookmarkDao.upsertBookmark(
                BookmarkEntity(
                    questionId = questionId,
                    createdAtEpochMillis = System.currentTimeMillis()
                )
            )
            analyticsLogger.logEvent("bookmark_added", mapOf("questionId" to questionId))
        }
    }
}
