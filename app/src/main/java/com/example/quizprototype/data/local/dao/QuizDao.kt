package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.QuizEntity
import com.example.quizprototype.data.local.model.QuizWithQuestions

@Dao
interface QuizDao {
    @Query("SELECT id FROM quizzes ORDER BY id LIMIT 1")
    suspend fun getFirstQuizId(): Int?

    @Transaction
    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizWithQuestions(quizId: Int): QuizWithQuestions?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(options: List<AnswerOptionEntity>)

    @Transaction
    suspend fun seedQuiz(
        quiz: QuizEntity,
        questions: List<QuestionEntity>,
        options: List<AnswerOptionEntity>
    ) {
        insertQuiz(quiz)
        insertQuestions(questions)
        insertAnswerOptions(options)
    }
}
