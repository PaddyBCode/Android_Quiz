package com.example.quizprototype.ui.study_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.domain.model.Category
import com.example.quizprototype.domain.model.QuestionQuery
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.StudyMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryStudyUiState(
    val categories: List<Category> = emptyList(),
    val isStarting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface CategoryStudyEvent {
    data class OpenSession(val sessionId: Long) : CategoryStudyEvent
}

class CategoryStudyViewModel(
    questionBankRepository: QuestionBankRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val starting = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _events = MutableSharedFlow<CategoryStudyEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<CategoryStudyUiState> = combine(
        questionBankRepository.observeCategories(),
        starting,
        errorMessage
    ) { categories, isStarting, error ->
        CategoryStudyUiState(
            categories = categories,
            isStarting = isStarting,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryStudyUiState()
    )

    fun clearError() {
        errorMessage.value = null
    }

    fun startCategorySession(category: Category) {
        viewModelScope.launch {
            starting.value = true
            errorMessage.value = null
            runCatching {
                studySessionRepository.startSession(
                    SessionConfig(
                        mode = StudyMode.PRACTICE,
                        title = "${category.title} Practice",
                        query = QuestionQuery(categoryIds = setOf(category.id)),
                        questionLimit = null,
                        durationLimitSeconds = null,
                        immediateFeedback = true,
                        allowReviewBeforeSubmit = true
                    )
                )
            }.onSuccess { sessionId ->
                _events.emit(CategoryStudyEvent.OpenSession(sessionId))
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to start category study."
            }
            starting.value = false
        }
    }

    companion object {
        fun provideFactory(
            questionBankRepository: QuestionBankRepository,
            studySessionRepository: StudySessionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CategoryStudyViewModel(
                        questionBankRepository = questionBankRepository,
                        studySessionRepository = studySessionRepository
                    ) as T
                }
            }
    }
}
