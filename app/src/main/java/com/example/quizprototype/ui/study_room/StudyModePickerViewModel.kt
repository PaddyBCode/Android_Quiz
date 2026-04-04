package com.example.quizprototype.ui.study_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.StudyMode
import com.example.quizprototype.domain.model.QuestionQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class StudyModePickerUiState(
    val isStarting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface StudyModePickerEvent {
    data class OpenSession(val sessionId: Long) : StudyModePickerEvent
}

class StudyModePickerViewModel(
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyModePickerUiState())
    private val _events = MutableSharedFlow<StudyModePickerEvent>()
    val events = _events.asSharedFlow()
    val uiState = _uiState

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun startPracticeSession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.PRACTICE,
                title = "Practice Session",
                query = QuestionQuery(),
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
                query = QuestionQuery(),
                questionLimit = 5,
                durationLimitSeconds = null,
                immediateFeedback = true,
                allowReviewBeforeSubmit = true
            )
        )
    }

    fun startMiniMockSession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.MOCK_EXAM,
                title = "Mini Mock",
                query = QuestionQuery(examEligibleOnly = true),
                questionLimit = 10,
                durationLimitSeconds = 15 * 60,
                immediateFeedback = false,
                allowReviewBeforeSubmit = false
            )
        )
    }

    fun startExamStyleMockSession() {
        startSession(
            config = SessionConfig(
                mode = StudyMode.MOCK_EXAM,
                title = "Exam Style Mock",
                query = QuestionQuery(examEligibleOnly = true),
                questionLimit = 30,
                durationLimitSeconds = 45 * 60,
                immediateFeedback = false,
                allowReviewBeforeSubmit = false
            )
        )
    }

    private fun startSession(config: SessionConfig) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isStarting = true, errorMessage = null)
            runCatching {
                studySessionRepository.startSession(config)
            }.onSuccess { sessionId ->
                _events.emit(StudyModePickerEvent.OpenSession(sessionId))
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isStarting = false,
                    errorMessage = throwable.message ?: "Unable to start session."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isStarting = false)
        }
    }

    companion object {
        fun provideFactory(
            studySessionRepository: StudySessionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return StudyModePickerViewModel(
                        studySessionRepository = studySessionRepository
                    ) as T
                }
            }
    }
}
