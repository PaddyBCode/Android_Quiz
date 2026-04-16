package com.example.quizprototype.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.AchievementsRepository
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.domain.model.Question
import com.example.quizprototype.domain.model.QuestionQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewQuestionsUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val showNotes: Boolean = false,
    val errorMessage: String? = null
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentIndex)
}

class ReviewQuestionsViewModel(
    private val scope: ReviewScope,
    private val filterId: String,
    private val questionBankRepository: QuestionBankRepository,
    private val achievementsRepository: AchievementsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewQuestionsUiState())
    val uiState: StateFlow<ReviewQuestionsUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    fun jumpToQuestion(index: Int) {
        val questions = _uiState.value.questions
        if (index !in questions.indices) return
        _uiState.value = _uiState.value.copy(currentIndex = index)
    }

    fun nextQuestion() {
        val nextIndex = (_uiState.value.currentIndex + 1).coerceAtMost(_uiState.value.questions.lastIndex)
        _uiState.value = _uiState.value.copy(currentIndex = nextIndex)
    }

    fun previousQuestion() {
        val previousIndex = (_uiState.value.currentIndex - 1).coerceAtLeast(0)
        _uiState.value = _uiState.value.copy(currentIndex = previousIndex)
    }

    fun toggleNotes() {
        _uiState.value = _uiState.value.copy(showNotes = !_uiState.value.showNotes)
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            runCatching {
                val query = when (scope) {
                    ReviewScope.ALL -> QuestionQuery()
                    ReviewScope.BOOKMARKED -> QuestionQuery(bookmarkedOnly = true)
                    ReviewScope.CATEGORY -> QuestionQuery(categoryIds = setOf(filterId))
                }
                val questions = questionBankRepository.getQuestions(query)
                require(questions.isNotEmpty()) { "No questions available for this review list." }
                val title = when (scope) {
                    ReviewScope.ALL -> "All Questions"
                    ReviewScope.BOOKMARKED -> "Bookmarked Questions"
                    ReviewScope.CATEGORY -> questions.first().categoryTitle
                }
                title to questions
            }.onSuccess { (title, questions) ->
                _uiState.value = ReviewQuestionsUiState(
                    isLoading = false,
                    title = title,
                    questions = questions
                )
                if (scope == ReviewScope.CATEGORY) {
                    achievementsRepository.onCategoryReviewOpened()
                }
            }.onFailure { throwable ->
                _uiState.value = ReviewQuestionsUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Unable to load review questions."
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            scope: ReviewScope,
            filterId: String,
            questionBankRepository: QuestionBankRepository,
            achievementsRepository: AchievementsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReviewQuestionsViewModel(
                        scope = scope,
                        filterId = filterId,
                        questionBankRepository = questionBankRepository,
                        achievementsRepository = achievementsRepository
                    ) as T
                }
            }
    }
}
