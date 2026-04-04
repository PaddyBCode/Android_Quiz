package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.BookmarkDao
import com.example.quizprototype.data.local.dao.QuestionBankDao
import com.example.quizprototype.data.local.dao.StudySessionDao
import com.example.quizprototype.data.local.entity.AnswerRecordEntity
import com.example.quizprototype.data.local.entity.StudySessionEntity
import com.example.quizprototype.domain.model.DashboardSummary
import com.example.quizprototype.domain.model.ProgressSnapshot
import com.example.quizprototype.domain.model.SessionResult
import com.example.quizprototype.domain.model.WeakCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DefaultProgressRepository(
    private val questionBankDao: QuestionBankDao,
    private val bookmarkDao: BookmarkDao,
    private val studySessionDao: StudySessionDao,
    private val studySessionRepository: StudySessionRepository
) : ProgressRepository {

    override fun observeDashboardSummary(): Flow<DashboardSummary> {
        return combine(
            questionBankDao.observeCategories(),
            bookmarkDao.observeBookmarkCount(),
            studySessionDao.observeCompletedSessions(),
            studySessionDao.observeCompletedAnswerRecords(),
            studySessionRepository.observeActiveSessionSummary()
        ) { categories, bookmarkCount, sessions, answers, activeSession ->
            val totalQuestions = questionBankDao.getQuestionCount()
            val recentResults = sessions.take(5).mapNotNull { session ->
                studySessionRepository.getResult(session.id)
            }
            val averageScorePercent = recentResults
                .mapNotNull { result ->
                    if (result.totalQuestions == 0) null else result.score * 100 / result.totalQuestions
                }
                .average()
                .let { value -> if (value.isNaN()) 0 else value.toInt() }
            val weakCategories = computeWeakCategories(categories.map { it.id to it.title }.toMap(), sessions, answers)
            val strongestCategory = weakCategories.maxByOrNull { it.accuracy }
            DashboardSummary(
                totalQuestions = totalQuestions,
                completedSessions = sessions.size,
                bookmarkedQuestions = bookmarkCount,
                weakQuestionsAvailable = studySessionDao.getWeakQuestionIds().size,
                readinessPercent = averageScorePercent.coerceIn(0, 100),
                averageScorePercent = averageScorePercent.coerceIn(0, 100),
                activeSession = activeSession,
                lastResult = recentResults.firstOrNull(),
                weakestCategories = weakCategories.take(3),
                focusNextCategory = weakCategories.firstOrNull(),
                strongestCategory = strongestCategory
            )
        }
    }

    override fun observeProgressSnapshot(): Flow<ProgressSnapshot> {
        return combine(
            questionBankDao.observeCategories(),
            studySessionDao.observeCompletedSessions(),
            studySessionDao.observeCompletedAnswerRecords()
        ) { categories, sessions, answers ->
            val categoryTitles = categories.associate { it.id to it.title }
            val recentResults = sessions.take(10).mapNotNull { session ->
                studySessionRepository.getResult(session.id)
            }
            val correctAnswers = answers.count { it.isCorrect }
            val averageScorePercent = recentResults
                .mapNotNull { result ->
                    if (result.totalQuestions == 0) null else result.score * 100 / result.totalQuestions
                }
                .average()
                .let { value -> if (value.isNaN()) 0 else value.toInt() }
            val weakestCategories = computeWeakCategories(categoryTitles, sessions, answers)
            ProgressSnapshot(
                completedSessions = sessions.size,
                totalAnsweredQuestions = answers.size,
                correctAnswers = correctAnswers,
                averageScorePercent = averageScorePercent.coerceIn(0, 100),
                strongestCategories = weakestCategories.sortedByDescending { it.accuracy }.take(3),
                weakestCategories = weakestCategories.take(3),
                recentResults = recentResults
            )
        }
    }

    private suspend fun computeWeakCategories(
        categoryTitles: Map<String, String>,
        sessions: List<StudySessionEntity>,
        answers: List<AnswerRecordEntity>
    ): List<WeakCategory> {
        if (sessions.isEmpty() || answers.isEmpty()) return emptyList()
        val sessionQuestionMap = sessions.associate { session ->
            session.id to studySessionDao.getSessionQuestions(session.id).map { it.questionId }
        }
        val questionsById = questionBankDao.getQuestions().associateBy { it.id }
        return answers.groupBy { answer ->
            questionsById[answer.questionId]?.categoryId.orEmpty()
        }
            .filterKeys { it.isNotBlank() }
            .map { (categoryId, categoryAnswers) ->
                WeakCategory(
                    categoryId = categoryId,
                    title = categoryTitles[categoryId] ?: categoryId,
                    accuracy = if (categoryAnswers.isEmpty()) 0f else {
                        categoryAnswers.count { it.isCorrect }.toFloat() / categoryAnswers.size.toFloat()
                    },
                    attempts = categoryAnswers.size
                )
            }
            .sortedBy { it.accuracy }
    }
}
