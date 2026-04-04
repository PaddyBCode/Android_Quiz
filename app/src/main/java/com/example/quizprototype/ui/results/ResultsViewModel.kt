package com.example.quizprototype.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.BookmarkRepository
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.domain.model.SessionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuestionReviewItem(
    val prompt: String,
    val categoryTitle: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val explanation: String,
    val wasCorrect: Boolean
)

data class ResultsUiState(
    val isLoading: Boolean = true,
    val result: SessionResult? = null,
    val reviewItems: List<QuestionReviewItem> = emptyList(),
    val incorrectQuestionIds: Set<String> = emptySet(),
    val isBookmarkingIncorrect: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)

class ResultsViewModel(
    sessionId: Long,
    private val studySessionRepository: StudySessionRepository,
    private val questionBankRepository: QuestionBankRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                val result = requireNotNull(studySessionRepository.getResult(sessionId))
                val questions = questionBankRepository.getQuestionsByIds(result.answerRecords.map { it.questionId })
                    .associateBy { it.id }
                val reviewItems = result.answerRecords.mapNotNull { answerRecord ->
                    val question = questions[answerRecord.questionId] ?: return@mapNotNull null
                    val selectedAnswer = question.options.firstOrNull { it.id == answerRecord.selectedOptionId }?.text
                        ?: "No answer selected"
                    val correctAnswer = question.options.first { it.id == question.correctOptionId }.text
                    QuestionReviewItem(
                        prompt = question.prompt,
                        categoryTitle = question.categoryTitle,
                        selectedAnswer = selectedAnswer,
                        correctAnswer = correctAnswer,
                        explanation = question.explanation,
                        wasCorrect = answerRecord.isCorrect
                    )
                }
                Triple(
                    result,
                    reviewItems,
                    result.answerRecords.filterNot { it.isCorrect }.map { it.questionId }.toSet()
                )
            }.onSuccess { (result, reviewItems, incorrectQuestionIds) ->
                _uiState.value = ResultsUiState(
                    isLoading = false,
                    result = result,
                    reviewItems = reviewItems,
                    incorrectQuestionIds = incorrectQuestionIds
                )
            }.onFailure { throwable ->
                _uiState.value = ResultsUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Unable to load results."
                )
            }
        }
    }

    fun bookmarkIncorrectQuestions() {
        val incorrectQuestionIds = uiState.value.incorrectQuestionIds
        if (incorrectQuestionIds.isEmpty() || uiState.value.isBookmarkingIncorrect) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBookmarkingIncorrect = true, message = null)
            runCatching {
                bookmarkRepository.addBookmarks(incorrectQuestionIds)
            }.onSuccess { addedCount ->
                _uiState.value = _uiState.value.copy(
                    isBookmarkingIncorrect = false,
                    message = if (addedCount == 0) {
                        "All incorrect questions are already bookmarked."
                    } else {
                        "$addedCount incorrect questions bookmarked."
                    }
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isBookmarkingIncorrect = false,
                    message = throwable.message ?: "Unable to bookmark incorrect questions."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    companion object {
        fun provideFactory(
            sessionId: Long,
            studySessionRepository: StudySessionRepository,
            questionBankRepository: QuestionBankRepository,
            bookmarkRepository: BookmarkRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ResultsViewModel(
                        sessionId = sessionId,
                        studySessionRepository = studySessionRepository,
                        questionBankRepository = questionBankRepository,
                        bookmarkRepository = bookmarkRepository
                    ) as T
                }
            }
    }
}
