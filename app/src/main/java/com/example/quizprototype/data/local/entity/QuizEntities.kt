package com.example.quizprototype.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String
)

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quizId")]
)
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val quizId: Int,
    val prompt: String,
    val orderIndex: Int
)

@Entity(
    tableName = "answer_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class AnswerOptionEntity(
    @PrimaryKey val id: Int,
    val questionId: Int,
    val text: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)
