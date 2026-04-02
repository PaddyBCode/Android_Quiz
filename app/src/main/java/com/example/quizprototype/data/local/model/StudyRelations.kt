package com.example.quizprototype.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity

data class QuestionWithOptions(
    @Embedded val question: QuestionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "questionId"
    )
    val options: List<AnswerOptionEntity>
)
