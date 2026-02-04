package com.example.quizprototype.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.QuizRepository
import com.example.quizprototype.domain.model.Quiz
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    var uiState by mutableStateOf(QuizUiState())
        private set

    private var loadedQuiz: Quiz? = null
    private val selectedAnswersByQuestionId = mutableMapOf<Int, Int>()

    init {
        loadQuiz()
    }

    fun onOptionSelected(optionId: Int) {
        uiState = uiState.copy(selectedOptionId = optionId)
    }

    fun onNextQuestion() {
        val quiz = loadedQuiz ?: return
        val currentQuestion = uiState.currentQuestion ?: return
        val selectedOptionId = uiState.selectedOptionId ?: return

        selectedAnswersByQuestionId[currentQuestion.id] = selectedOptionId

        val nextIndex = uiState.questionIndex + 1
        val isLastQuestion = nextIndex >= quiz.questions.size

        if (isLastQuestion) {
            val finalScore = quiz.questions.count { question ->
                selectedAnswersByQuestionId[question.id] == question.correctOptionId
            }
            uiState = uiState.copy(
                score = finalScore,
                isQuizCompleted = true,
                selectedOptionId = null
            )
            return
        }

        val nextQuestion = quiz.questions[nextIndex]
        uiState = uiState.copy(
            questionIndex = nextIndex,
            currentQuestion = nextQuestion,
            selectedOptionId = selectedAnswersByQuestionId[nextQuestion.id]
        )
    }

    fun onRestartQuiz() {
        val quiz = loadedQuiz ?: return
        selectedAnswersByQuestionId.clear()
        uiState = uiState.copy(
            questionIndex = 0,
            currentQuestion = quiz.questions.firstOrNull(),
            selectedOptionId = null,
            score = 0,
            isQuizCompleted = false,
            errorMessage = null
        )
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            runCatching {
                quizRepository.seedIfNeeded()
                quizRepository.getFirstQuiz()
            }.onSuccess { quiz ->
                if (quiz == null || quiz.questions.isEmpty()) {
                    uiState = QuizUiState(
                        isLoading = false,
                        errorMessage = "No quiz data available."
                    )
                    return@onSuccess
                }

                loadedQuiz = quiz
                uiState = QuizUiState(
                    isLoading = false,
                    title = quiz.title,
                    description = quiz.description,
                    currentQuestion = quiz.questions.first(),
                    questionIndex = 0,
                    totalQuestions = quiz.questions.size
                )
            }.onFailure {
                uiState = QuizUiState(
                    isLoading = false,
                    errorMessage = "Failed to load quiz data."
                )
            }
        }
    }

    companion object {
        fun provideFactory(repository: QuizRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuizViewModel(repository) as T
                }
            }
    }
}
