package com.example.quizprototype.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.QuizEntity

data class QuizWithQuestions(
    @Embedded val quiz: QuizEntity,
    @Relation(
        entity = QuestionEntity::class,
        parentColumn = "id",
        entityColumn = "quizId"
    )
    val questions: List<QuestionWithOptions>
)

data class QuestionWithOptions(
    @Embedded val question: QuestionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionId"
    )
    val options: List<AnswerOptionEntity>
)
