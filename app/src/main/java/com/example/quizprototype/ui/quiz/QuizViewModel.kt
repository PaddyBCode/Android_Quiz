package com.example.quizprototype.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.QuizRepository
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.Quiz
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    var uiState by mutableStateOf(QuizUiState())
        private set

    private var quickQuizQuestions: List<Question> = emptyList()
    private var loadedQuiz: Quiz? = null
    private val selectedAnswersByQuestionId = mutableMapOf<Int, Int>()

    init {
        loadQuiz()
    }

    fun onOptionSelected(optionId: Int) {
        uiState = uiState.copy(selectedOptionId = optionId)
    }

    fun onNextQuestion() {
        val questions = quickQuizQuestions
        if (questions.isEmpty()) return
        val currentQuestion = uiState.currentQuestion ?: return
        val selectedOptionId = uiState.selectedOptionId ?: return

        selectedAnswersByQuestionId[currentQuestion.id] = selectedOptionId

        val nextIndex = uiState.questionIndex + 1
        val isLastQuestion = nextIndex >= questions.size

        if (isLastQuestion) {
            val finalScore = questions.count { question ->
                selectedAnswersByQuestionId[question.id] == question.correctOptionId
            }
            uiState = uiState.copy(
                score = finalScore,
                isQuizCompleted = true,
                selectedOptionId = null
            )
            return
        }

        val nextQuestion = questions[nextIndex]
        uiState = uiState.copy(
            questionIndex = nextIndex,
            currentQuestion = nextQuestion,
            selectedOptionId = selectedAnswersByQuestionId[nextQuestion.id]
        )
    }

    fun onRestartQuiz() {
        val quiz = loadedQuiz ?: return
        startQuickQuizSession(quiz)
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
                startQuickQuizSession(quiz)
            }.onFailure { throwable ->
                val reason = throwable.message?.takeIf { it.isNotBlank() } ?: "Unknown error"
                uiState = QuizUiState(
                    isLoading = false,
                    errorMessage = "Failed to load quiz data: $reason"
                )
            }
        }
    }

    private fun startQuickQuizSession(quiz: Quiz) {
        quickQuizQuestions = quiz.questions
            .shuffled()
            .take(minOf(QUICK_QUIZ_QUESTION_COUNT, quiz.questions.size))

        if (quickQuizQuestions.isEmpty()) {
            uiState = QuizUiState(
                isLoading = false,
                errorMessage = "No quick quiz questions available."
            )
            return
        }

        selectedAnswersByQuestionId.clear()
        uiState = QuizUiState(
            isLoading = false,
            title = "Quick Quiz",
            description = "5 random questions from all categories.",
            currentQuestion = quickQuizQuestions.first(),
            questionIndex = 0,
            totalQuestions = quickQuizQuestions.size
        )
    }

    companion object {
        private const val QUICK_QUIZ_QUESTION_COUNT = 5

        fun provideFactory(repository: QuizRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuizViewModel(repository) as T
                }
            }
    }
}
