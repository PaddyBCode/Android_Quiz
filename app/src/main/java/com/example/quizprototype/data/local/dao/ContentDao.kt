package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity

@Dao
interface ContentDao {
    @Query("SELECT * FROM content_versions WHERE id = 1")
    suspend fun getContentVersion(): ContentVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentVersion(contentVersion: ContentVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(options: List<AnswerOptionEntity>)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Query("DELETE FROM answer_options")
    suspend fun deleteAnswerOptions()

    @Query("DELETE FROM questions")
    suspend fun deleteQuestions()

    @Query("DELETE FROM topics")
    suspend fun deleteTopics()

    @Query("DELETE FROM categories")
    suspend fun deleteCategories()

    @Query("DELETE FROM content_versions")
    suspend fun deleteContentVersions()

    @Transaction
    suspend fun replaceAllContent(
        contentVersion: ContentVersionEntity,
        categories: List<CategoryEntity>,
        topics: List<TopicEntity>,
        questions: List<QuestionEntity>,
        options: List<AnswerOptionEntity>
    ) {
        deleteAnswerOptions()
        deleteQuestions()
        deleteTopics()
        deleteCategories()
        deleteContentVersions()
        insertCategories(categories)
        insertTopics(topics)
        insertQuestions(questions)
        insertAnswerOptions(options)
        insertContentVersion(contentVersion)
    }
}
