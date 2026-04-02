package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.QuizDao
import com.example.quizprototype.data.local.model.QuizWithQuestions
import com.example.quizprototype.data.seed.SampleQuizData
import com.example.quizprototype.domain.model.AnswerOption
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.Quiz

class DefaultQuizRepository(
    private val quizDao: QuizDao
) : QuizRepository {

    override suspend fun seedIfNeeded() {
        val existingQuizId = quizDao.getFirstQuizId()
        if (existingQuizId == null) {
            quizDao.seedQuiz(
                quiz = SampleQuizData.quiz,
                questions = SampleQuizData.questions,
                options = SampleQuizData.options
            )
            return
        }

        val existingQuestionCount = quizDao.getQuestionCountForQuiz(existingQuizId)
        if (existingQuestionCount >= SampleQuizData.questions.size) return

        quizDao.replaceSeedQuiz(
            quiz = SampleQuizData.quiz,
            questions = SampleQuizData.questions,
            options = SampleQuizData.options
        )
    }

    override suspend fun getFirstQuiz(): Quiz? {
        val quizId = quizDao.getFirstQuizId() ?: return null
        return quizDao.getQuizWithQuestions(quizId)?.toDomainModel()
    }
}

private fun QuizWithQuestions.toDomainModel(): Quiz {
    val orderedQuestions = questions
        .sortedBy { it.question.orderIndex }
        .map { questionWithOptions ->
            val orderedOptions = questionWithOptions.options.sortedBy { it.orderIndex }
            val correctOptionId = orderedOptions.first { it.isCorrect }.id
            Question(
                id = questionWithOptions.question.id,
                prompt = questionWithOptions.question.prompt,
                category = questionWithOptions.question.category,
                options = orderedOptions.map { option ->
                    AnswerOption(
                        id = option.id,
                        text = option.text
                    )
                },
                correctOptionId = correctOptionId
            )
        }

    return Quiz(
        id = quiz.id,
        title = quiz.title,
        description = quiz.description,
        questions = orderedQuestions
    )
}
