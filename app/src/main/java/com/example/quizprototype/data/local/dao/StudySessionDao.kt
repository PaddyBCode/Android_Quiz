package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.quizprototype.data.local.entity.AnswerRecordEntity
import com.example.quizprototype.data.local.entity.StudySessionEntity
import com.example.quizprototype.data.local.entity.StudySessionQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert
    suspend fun insertSession(session: StudySessionEntity): Long

    @Insert
    suspend fun insertSessionQuestions(sessionQuestions: List<StudySessionQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnswerRecord(answerRecord: AnswerRecordEntity)

    @Query("UPDATE study_sessions SET currentIndex = :currentIndex WHERE id = :sessionId")
    suspend fun updateCurrentIndex(sessionId: Long, currentIndex: Int)

    @Query(
        "UPDATE study_sessions SET completedAtEpochMillis = :completedAtEpochMillis, currentIndex = :currentIndex WHERE id = :sessionId"
    )
    suspend fun completeSession(
        sessionId: Long,
        completedAtEpochMillis: Long,
        currentIndex: Int
    )

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    fun observeSession(sessionId: Long): Flow<StudySessionEntity?>

    @Query("SELECT * FROM study_sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: Long): StudySessionEntity?

    @Query("SELECT * FROM study_session_questions WHERE sessionId = :sessionId ORDER BY orderIndex")
    fun observeSessionQuestions(sessionId: Long): Flow<List<StudySessionQuestionEntity>>

    @Query("SELECT * FROM study_session_questions WHERE sessionId = :sessionId ORDER BY orderIndex")
    suspend fun getSessionQuestions(sessionId: Long): List<StudySessionQuestionEntity>

    @Query("SELECT * FROM answer_records WHERE sessionId = :sessionId")
    fun observeAnswerRecords(sessionId: Long): Flow<List<AnswerRecordEntity>>

    @Query("SELECT * FROM answer_records WHERE sessionId = :sessionId")
    suspend fun getAnswerRecords(sessionId: Long): List<AnswerRecordEntity>

    @Query(
        "SELECT * FROM study_sessions WHERE completedAtEpochMillis IS NULL ORDER BY startedAtEpochMillis DESC LIMIT 1"
    )
    fun observeActiveSession(): Flow<StudySessionEntity?>

    @Query(
        "SELECT * FROM study_sessions WHERE completedAtEpochMillis IS NULL ORDER BY startedAtEpochMillis DESC LIMIT 1"
    )
    suspend fun getActiveSession(): StudySessionEntity?

    @Query(
        "SELECT * FROM study_sessions WHERE completedAtEpochMillis IS NOT NULL ORDER BY completedAtEpochMillis DESC"
    )
    fun observeCompletedSessions(): Flow<List<StudySessionEntity>>

    @Query(
        "SELECT * FROM study_sessions WHERE completedAtEpochMillis IS NOT NULL ORDER BY completedAtEpochMillis DESC"
    )
    suspend fun getCompletedSessions(): List<StudySessionEntity>

    @Query(
        "SELECT * FROM answer_records WHERE sessionId IN (SELECT id FROM study_sessions WHERE completedAtEpochMillis IS NOT NULL)"
    )
    fun observeCompletedAnswerRecords(): Flow<List<AnswerRecordEntity>>

    @Query(
        "SELECT questionId FROM answer_records GROUP BY questionId HAVING SUM(CASE WHEN isCorrect THEN 0 ELSE 1 END) > 0 ORDER BY SUM(CASE WHEN isCorrect THEN 0 ELSE 1 END) DESC, MAX(responseTimeMillis) DESC"
    )
    suspend fun getWeakQuestionIds(): List<String>

    @Transaction
    suspend fun createSession(
        session: StudySessionEntity,
        sessionQuestions: List<StudySessionQuestionEntity>
    ): Long {
        val sessionId = insertSession(session)
        insertSessionQuestions(
            sessionQuestions.map { it.copy(sessionId = sessionId) }
        )
        return sessionId
    }
}
