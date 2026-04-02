package com.example.quizprototype.ui.study

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

data class StudyModePickerUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryIds: Set<String> = emptySet(),
    val isStarting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface StudyModePickerEvent {
    data class OpenSession(val sessionId: Long) : StudyModePickerEvent
}

class StudyModePickerViewModel(
    questionBankRepository: QuestionBankRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    private val starting = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _events = MutableSharedFlow<StudyModePickerEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<StudyModePickerUiState> = combine(
        questionBankRepository.observeCategories(),
        selectedCategoryIds,
        starting,
        errorMessage
    ) { categories, selectedIds, isStarting, error ->
        StudyModePickerUiState(
            categories = categories,
            selectedCategoryIds = selectedIds,
            isStarting = isStarting,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StudyModePickerUiState()
    )

    fun toggleCategory(categoryId: String) {
        selectedCategoryIds.value = selectedCategoryIds.value.toMutableSet().apply {
            if (!add(categoryId)) {
                remove(categoryId)
            }
        }
    }

    fun clearSelection() {
        selectedCategoryIds.value = emptySet()
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun startPracticeSession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.PRACTICE,
                title = if (selectedCategoryIds.value.isEmpty()) "Practice Session" else "Category Practice",
                query = QuestionQuery(categoryIds = selectedCategoryIds.value),
                questionLimit = null,
                durationLimitSeconds = null,
                immediateFeedback = true,
                allowReviewBeforeSubmit = true
            )
        )
    }

    fun startQuickStudySession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.QUICK_STUDY,
                title = "Quick Study",
                query = QuestionQuery(categoryIds = selectedCategoryIds.value),
                questionLimit = 5,
                durationLimitSeconds = null,
                immediateFeedback = true,
                allowReviewBeforeSubmit = true
            )
        )
    }

    fun startMockExamSession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.MOCK_EXAM,
                title = "Mock Exam",
                query = QuestionQuery(
                    categoryIds = selectedCategoryIds.value,
                    examEligibleOnly = true
                ),
                questionLimit = 10,
                durationLimitSeconds = 15 * 60,
                immediateFeedback = false,
                allowReviewBeforeSubmit = false
            )
        )
    }

    private fun startSession(config: SessionConfig) {
        viewModelScope.launch {
            starting.value = true
            errorMessage.value = null
            runCatching {
                studySessionRepository.startSession(config)
            }.onSuccess { sessionId ->
                _events.emit(StudyModePickerEvent.OpenSession(sessionId))
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to start session."
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
                    return StudyModePickerViewModel(
                        questionBankRepository = questionBankRepository,
                        studySessionRepository = studySessionRepository
                    ) as T
                }
            }
    }
}
