package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.BookmarkDao
import com.example.quizprototype.data.local.dao.QuestionBankDao
import com.example.quizprototype.data.local.dao.StudySessionDao
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.TopicEntity
import com.example.quizprototype.data.local.model.QuestionWithOptions
import com.example.quizprototype.domain.model.AnswerOption
import com.example.quizprototype.domain.model.Category
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.QuestionQuery
import com.example.quizprototype.domain.model.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultQuestionBankRepository(
    private val questionBankDao: QuestionBankDao,
    private val bookmarkDao: BookmarkDao,
    private val studySessionDao: StudySessionDao
) : QuestionBankRepository {

    override fun observeCategories(): Flow<List<Category>> {
        return questionBankDao.observeCategories().map { categories ->
            val questions = questionBankDao.getQuestions()
            val completedAnswers = studySessionDao.getWeakQuestionIds().toSet()
            categories.map { category ->
                val categoryQuestions = questions.filter { it.categoryId == category.id }
                Category(
                    id = category.id,
                    licenceType = category.licenceType,
                    title = category.title,
                    description = category.description,
                    questionCount = categoryQuestions.size,
                    correctRate = if (categoryQuestions.isEmpty()) {
                        null
                    } else {
                        val weakCount = categoryQuestions.count { completedAnswers.contains(it.id) }
                        ((categoryQuestions.size - weakCount).toFloat() / categoryQuestions.size.toFloat())
                    }
                )
            }
        }
    }

    override suspend fun getTopicsForCategory(categoryId: String): List<Topic> {
        val topics = questionBankDao.getTopicsForCategory(categoryId)
        val questions = questionBankDao.getQuestions()
        return topics.map { topic ->
            Topic(
                id = topic.id,
                categoryId = topic.categoryId,
                title = topic.title,
                description = topic.description,
                questionCount = questions.count { it.topicId == topic.id }
            )
        }
    }

    override suspend fun getQuestions(query: QuestionQuery): List<Question> {
        val bookmarkedIds = if (query.bookmarkedOnly) bookmarkDao.getBookmarkedQuestionIds().toSet() else emptySet()
        val weakIds = if (query.weakOnly) studySessionDao.getWeakQuestionIds().toSet() else emptySet()
        return getAllQuestions()
            .asSequence()
            .filter { it.licenceType == query.licenceType }
            .filter { query.categoryIds.isEmpty() || query.categoryIds.contains(it.categoryId) }
            .filter { query.topicIds.isEmpty() || query.topicIds.contains(it.topicId) }
            .filter { !query.examEligibleOnly || it.isExamEligible }
            .filter { !query.bookmarkedOnly || bookmarkedIds.contains(it.id) }
            .filter { !query.weakOnly || weakIds.contains(it.id) }
            .let { sequence ->
                query.limit?.let { limit -> sequence.take(limit) } ?: sequence
            }
            .toList()
    }

    override suspend fun getQuestionsByIds(questionIds: List<String>): List<Question> {
        if (questionIds.isEmpty()) return emptyList()
        val categoryMap = questionBankDao.getCategories().associateBy { it.id }
        val topicMap = questionBankDao.getTopics().associateBy { it.id }
        val questionsById = questionBankDao.getQuestionsWithOptionsByIds(questionIds)
            .associateBy { it.question.id }
        return questionIds.mapNotNull { questionId ->
            questionsById[questionId]?.toDomainQuestion(categoryMap, topicMap)
        }
    }

    override suspend fun getQuestion(questionId: String): Question? {
        val categoryMap = questionBankDao.getCategories().associateBy { it.id }
        val topicMap = questionBankDao.getTopics().associateBy { it.id }
        return questionBankDao.getQuestionWithOptions(questionId)?.toDomainQuestion(categoryMap, topicMap)
    }

    override suspend fun getQuestionCount(): Int = questionBankDao.getQuestionCount()

    private suspend fun getAllQuestions(): List<Question> {
        val categoryMap = questionBankDao.getCategories().associateBy { it.id }
        val topicMap = questionBankDao.getTopics().associateBy { it.id }
        return questionBankDao.getQuestionsWithOptions().mapNotNull { relation ->
            relation.toDomainQuestion(categoryMap, topicMap)
        }
    }
}

private fun QuestionWithOptions.toDomainQuestion(
    categoryMap: Map<String, CategoryEntity>,
    topicMap: Map<String, TopicEntity>
): Question? {
    val category = categoryMap[question.categoryId] ?: return null
    val topic = topicMap[question.topicId] ?: return null
    val sortedOptions = options.sortedBy { it.sortOrder }
    val correctOptionId = sortedOptions.firstOrNull { it.isCorrect }?.id ?: return null
    return Question(
        id = question.id,
        licenceType = question.licenceType,
        categoryId = question.categoryId,
        categoryTitle = category.title,
        topicId = question.topicId,
        topicTitle = topic.title,
        prompt = question.prompt,
        explanation = question.explanation,
        sourceReference = question.sourceReference,
        assetName = question.assetName,
        isExamEligible = question.isExamEligible,
        options = sortedOptions.map { option ->
            AnswerOption(id = option.id, text = option.text)
        },
        correctOptionId = correctOptionId
    )
}
