package com.example.quizprototype.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.BookmarkRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.domain.model.ActiveStudySession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SessionUiState(
    val session: ActiveStudySession? = null,
    val remainingSeconds: Int? = null,
    val errorMessage: String? = null
) {
    val isLoading: Boolean
        get() = session == null && errorMessage == null
}

sealed interface SessionEvent {
    data class OpenResults(val sessionId: Long) : SessionEvent
}

class SessionViewModel(
    private val sessionId: Long,
    private val studySessionRepository: StudySessionRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {
    private val errorMessage = MutableStateFlow<String?>(null)
    private val remainingSeconds = MutableStateFlow<Int?>(null)
    private val _events = MutableSharedFlow<SessionEvent>()
    val events = _events.asSharedFlow()

    private var currentQuestionShownAt = System.currentTimeMillis()
    private var trackedQuestionId: String? = null
    private var timerStarted = false
    private var autoCompleted = false

    val uiState: StateFlow<SessionUiState> = combine(
        studySessionRepository.observeSession(sessionId),
        remainingSeconds,
        errorMessage
    ) { session, seconds, error ->
        session?.currentQuestion?.question?.id?.let { questionId ->
            if (questionId != trackedQuestionId) {
                trackedQuestionId = questionId
                currentQuestionShownAt = System.currentTimeMillis()
            }
        }
        if (session != null && session.durationLimitSeconds != null && !timerStarted) {
            timerStarted = true
            startTimer()
        }
        SessionUiState(
            session = session,
            remainingSeconds = seconds,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionUiState()
    )

    fun selectOption(optionId: String) {
        val currentQuestion = uiState.value.session?.currentQuestion ?: return
        if (currentQuestion.selectedOptionId != null) return
        viewModelScope.launch {
            studySessionRepository.submitAnswer(
                sessionId = sessionId,
                questionId = currentQuestion.question.id,
                selectedOptionId = optionId,
                responseTimeMillis = System.currentTimeMillis() - currentQuestionShownAt
            )
        }
    }

    fun nextQuestion() {
        val session = uiState.value.session ?: return
        val nextIndex = session.currentIndex + 1
        if (nextIndex >= session.totalQuestions) {
            finishSession()
            return
        }
        viewModelScope.launch {
            studySessionRepository.updateCurrentIndex(session.id, nextIndex)
        }
    }

    fun jumpToQuestion(index: Int) {
        val session = uiState.value.session ?: return
        if (index !in session.questions.indices) return
        viewModelScope.launch {
            studySessionRepository.updateCurrentIndex(session.id, index)
        }
    }

    fun toggleBookmark() {
        val questionId = uiState.value.session?.currentQuestion?.question?.id ?: return
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(questionId)
        }
    }

    fun finishSession() {
        if (autoCompleted) return
        viewModelScope.launch {
            runCatching {
                studySessionRepository.completeSession(sessionId)
            }.onSuccess { result ->
                autoCompleted = true
                _events.emit(SessionEvent.OpenResults(result.sessionId))
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to finish session."
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val session = uiState.value.session ?: break
                val limit = session.durationLimitSeconds ?: break
                val elapsed = ((System.currentTimeMillis() - session.startedAtEpochMillis) / 1000L).toInt()
                val remaining = (limit - elapsed).coerceAtLeast(0)
                remainingSeconds.value = remaining
                if (remaining == 0 && !autoCompleted) {
                    finishSession()
                    break
                }
                delay(1_000)
            }
        }
    }

    companion object {
        fun provideFactory(
            sessionId: Long,
            studySessionRepository: StudySessionRepository,
            bookmarkRepository: BookmarkRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SessionViewModel(
                        sessionId = sessionId,
                        studySessionRepository = studySessionRepository,
                        bookmarkRepository = bookmarkRepository
                    ) as T
                }
            }
    }
}
