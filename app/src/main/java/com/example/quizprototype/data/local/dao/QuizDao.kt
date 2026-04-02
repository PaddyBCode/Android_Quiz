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

    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionCountForQuiz(quizId: Int): Int

    @Transaction
    @Query("SELECT * FROM quizzes WHERE id = :quizId")
    suspend fun getQuizWithQuestions(quizId: Int): QuizWithQuestions?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(options: List<AnswerOptionEntity>)

    @Query("DELETE FROM answer_options")
    suspend fun deleteAllAnswerOptions()

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM quizzes")
    suspend fun deleteAllQuizzes()

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

    @Transaction
    suspend fun replaceSeedQuiz(
        quiz: QuizEntity,
        questions: List<QuestionEntity>,
        options: List<AnswerOptionEntity>
    ) {
        deleteAllAnswerOptions()
        deleteAllQuestions()
        deleteAllQuizzes()
        seedQuiz(
            quiz = quiz,
            questions = questions,
            options = options
        )
    }
}
