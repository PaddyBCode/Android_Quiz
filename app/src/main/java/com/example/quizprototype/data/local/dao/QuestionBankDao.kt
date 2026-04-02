package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity
import com.example.quizprototype.data.local.model.QuestionWithOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionBankDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder")
    suspend fun getCategories(): List<CategoryEntity>

    @Query("SELECT * FROM topics ORDER BY sortOrder")
    suspend fun getTopics(): List<TopicEntity>

    @Query("SELECT * FROM topics WHERE categoryId = :categoryId ORDER BY sortOrder")
    suspend fun getTopicsForCategory(categoryId: String): List<TopicEntity>

    @Query("SELECT * FROM questions")
    suspend fun getQuestions(): List<QuestionEntity>

    @Transaction
    @Query("SELECT * FROM questions ORDER BY sortOrder")
    suspend fun getQuestionsWithOptions(): List<QuestionWithOptions>

    @Transaction
    @Query("SELECT * FROM questions WHERE id IN (:questionIds)")
    suspend fun getQuestionsWithOptionsByIds(questionIds: List<String>): List<QuestionWithOptions>

    @Transaction
    @Query("SELECT * FROM questions WHERE id = :questionId")
    suspend fun getQuestionWithOptions(questionId: String): QuestionWithOptions?

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int
}
