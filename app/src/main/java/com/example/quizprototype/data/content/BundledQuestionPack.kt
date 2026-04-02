package com.example.quizprototype.data.content

import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity

data class BundledQuestionPack(
    val contentVersion: ContentVersionEntity,
    val categories: List<CategoryEntity>,
    val topics: List<TopicEntity>,
    val questions: List<QuestionEntity>,
    val options: List<AnswerOptionEntity>
)
