package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.BookmarkDao
import com.example.quizprototype.data.local.dao.QuestionBankDao
import com.example.quizprototype.data.local.dao.StudySessionDao
import com.example.quizprototype.data.local.entity.AnswerRecordEntity
import com.example.quizprototype.data.local.entity.StudySessionEntity
import com.example.quizprototype.data.local.entity.StudySessionQuestionEntity
import com.example.quizprototype.domain.model.ActiveStudySession
import com.example.quizprototype.domain.model.AnswerRecord
import com.example.quizprototype.domain.model.CategoryPerformance
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.SessionQuestion
import com.example.quizprototype.domain.model.SessionResult
import com.example.quizprototype.domain.model.SessionSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DefaultStudySessionRepository(
    private val studySessionDao: StudySessionDao,
    private val questionBankRepository: QuestionBankRepository,
    private val questionBankDao: QuestionBankDao,
    private val bookmarkDao: BookmarkDao,
    private val achievementsRepository: AchievementsRepository,
    private val analyticsLogger: AnalyticsLogger
) : StudySessionRepository {

    override fun observeActiveSessionSummary(): Flow<SessionSummary?> {
        return studySessionDao.observeActiveSession().map { session -> session?.toSummary() }
    }

    override fun observeSession(sessionId: Long): Flow<ActiveStudySession?> {
        return combine(
            studySessionDao.observeSession(sessionId),
            studySessionDao.observeSessionQuestions(sessionId),
            studySessionDao.observeAnswerRecords(sessionId),
            bookmarkDao.observeBookmarks()
        ) { session, sessionQuestions, answerRecords, bookmarks ->
            if (session == null) {
                return@combine null
            }
            val questions = questionBankRepository.getQuestionsByIds(sessionQuestions.map { it.questionId })
                .associateBy { it.id }
            val answersByQuestionId = answerRecords.associateBy { it.questionId }
            val bookmarkedIds = bookmarks.map { it.questionId }.toSet()
            val orderedQuestions = sessionQuestions.sortedBy { it.orderIndex }.mapNotNull { sessionQuestion ->
                val question = questions[sessionQuestion.questionId] ?: return@mapNotNull null
                SessionQuestion(
                    orderIndex = sessionQuestion.orderIndex,
                    question = question,
                    selectedOptionId = answersByQuestionId[sessionQuestion.questionId]?.selectedOptionId,
                    isBookmarked = bookmarkedIds.contains(sessionQuestion.questionId)
                )
            }
            ActiveStudySession(
                id = session.id,
                mode = session.mode,
                title = session.title,
                questions = orderedQuestions,
                currentIndex = session.currentIndex.coerceIn(0, (orderedQuestions.size - 1).coerceAtLeast(0)),
                startedAtEpochMillis = session.startedAtEpochMillis,
                completedAtEpochMillis = session.completedAtEpochMillis,
                durationLimitSeconds = session.durationLimitSeconds,
                immediateFeedback = session.immediateFeedback,
                allowReviewBeforeSubmit = session.allowReviewBeforeSubmit
            )
        }
    }

    override suspend fun getActiveSessionId(): Long? = studySessionDao.getActiveSession()?.id

    override suspend fun startSession(config: SessionConfig): Long {
        val questions = questionBankRepository.getQuestions(config.query)
            .shuffled()
            .let { selected ->
                config.questionLimit?.let { limit -> selected.take(limit) } ?: selected
            }
        require(questions.isNotEmpty()) { "No questions available for this study mode." }

        val now = System.currentTimeMillis()
        val sessionId = studySessionDao.createSession(
            session = StudySessionEntity(
                mode = config.mode,
                title = config.title,
                startedAtEpochMillis = now,
                completedAtEpochMillis = null,
                currentIndex = 0,
                totalQuestions = questions.size,
                durationLimitSeconds = config.durationLimitSeconds,
                immediateFeedback = config.immediateFeedback,
                allowReviewBeforeSubmit = config.allowReviewBeforeSubmit
            ),
            sessionQuestions = questions.mapIndexed { index, question ->
                StudySessionQuestionEntity(
                    sessionId = 0,
                    questionId = question.id,
                    orderIndex = index
                )
            }
        )
        analyticsLogger.logEvent(
            "session_started",
            mapOf(
                "sessionId" to sessionId.toString(),
                "mode" to config.mode.name,
                "questionCount" to questions.size.toString()
            )
        )
        return sessionId
    }

    override suspend fun submitAnswer(
        sessionId: Long,
        questionId: String,
        selectedOptionId: String,
        responseTimeMillis: Long
    ) {
        val question = questionBankRepository.getQuestion(questionId) ?: return
        studySessionDao.upsertAnswerRecord(
            AnswerRecordEntity(
                sessionId = sessionId,
                questionId = questionId,
                selectedOptionId = selectedOptionId,
                isCorrect = selectedOptionId == question.correctOptionId,
                answeredAtEpochMillis = System.currentTimeMillis(),
                responseTimeMillis = responseTimeMillis
            )
        )
        analyticsLogger.logEvent(
            "question_answered",
            mapOf(
                "sessionId" to sessionId.toString(),
                "questionId" to questionId,
                "correct" to (selectedOptionId == question.correctOptionId).toString()
            )
        )
    }

    override suspend fun updateCurrentIndex(sessionId: Long, currentIndex: Int) {
        studySessionDao.updateCurrentIndex(sessionId = sessionId, currentIndex = currentIndex)
    }

    override suspend fun completeSession(sessionId: Long): SessionResult {
        val session = studySessionDao.getSession(sessionId) ?: error("Session $sessionId not found.")
        val questionIds = studySessionDao.getSessionQuestions(sessionId).sortedBy { it.orderIndex }.map { it.questionId }
        val finalIndex = (questionIds.size - 1).coerceAtLeast(0)
        studySessionDao.completeSession(
            sessionId = sessionId,
            completedAtEpochMillis = System.currentTimeMillis(),
            currentIndex = finalIndex
        )
        val result = requireNotNull(getResult(sessionId))
        analyticsLogger.logEvent(
            "session_finished",
            mapOf(
                "sessionId" to sessionId.toString(),
                "mode" to session.mode.name,
                "score" to result.score.toString(),
                "total" to result.totalQuestions.toString()
            )
        )
        achievementsRepository.onSessionCompleted(result)
        return result
    }

    override suspend fun getResult(sessionId: Long): SessionResult? {
        val session = studySessionDao.getSession(sessionId) ?: return null
        val questionIds = studySessionDao.getSessionQuestions(sessionId).sortedBy { it.orderIndex }.map { it.questionId }
        val questions = questionBankRepository.getQuestionsByIds(questionIds).associateBy { it.id }
        val answers = studySessionDao.getAnswerRecords(sessionId).map { it.toDomain() }
        val orderedQuestions = questionIds.mapNotNull { questions[it] }
        val score = orderedQuestions.count { question ->
            answers.firstOrNull { it.questionId == question.id }?.isCorrect == true
        }
        val durationSeconds = ((session.completedAtEpochMillis ?: System.currentTimeMillis()) - session.startedAtEpochMillis)
            .coerceAtLeast(0L)
            .div(1000L)
            .toInt()
        val categoryBreakdown = orderedQuestions.groupBy { it.categoryId }.map { (categoryId, categoryQuestions) ->
            val title = categoryQuestions.first().categoryTitle
            val correctAnswers = categoryQuestions.count { question ->
                answers.firstOrNull { it.questionId == question.id }?.isCorrect == true
            }
            CategoryPerformance(
                categoryId = categoryId,
                title = title,
                correctAnswers = correctAnswers,
                totalAnswers = categoryQuestions.size
            )
        }.sortedBy { it.title }
        return SessionResult(
            sessionId = session.id,
            title = session.title,
            mode = session.mode,
            score = score,
            totalQuestions = orderedQuestions.size,
            durationSeconds = durationSeconds,
            passed = orderedQuestions.isNotEmpty() && score * 100 >= orderedQuestions.size * PASS_THRESHOLD_PERCENT,
            categoryBreakdown = categoryBreakdown,
            answerRecords = answers.sortedBy { answer ->
                questionIds.indexOf(answer.questionId)
            }
        )
    }

    override suspend fun getWeakQuestionIds(): Set<String> = studySessionDao.getWeakQuestionIds().toSet()

    private fun StudySessionEntity.toSummary(): SessionSummary {
        return SessionSummary(
            sessionId = id,
            title = title,
            mode = mode,
            currentIndex = currentIndex,
            totalQuestions = totalQuestions,
            startedAtEpochMillis = startedAtEpochMillis,
            durationLimitSeconds = durationLimitSeconds
        )
    }

    private fun AnswerRecordEntity.toDomain(): AnswerRecord {
        return AnswerRecord(
            sessionId = sessionId,
            questionId = questionId,
            selectedOptionId = selectedOptionId,
            isCorrect = isCorrect,
            answeredAtEpochMillis = answeredAtEpochMillis,
            responseTimeMillis = responseTimeMillis
        )
    }

    companion object {
        private const val PASS_THRESHOLD_PERCENT = 75
    }
}
