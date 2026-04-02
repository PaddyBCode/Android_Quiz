package com.example.quizprototype.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.ProgressRepository
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.data.repository.UserProfileRepository
import com.example.quizprototype.domain.model.Category
import com.example.quizprototype.domain.model.DashboardSummary
import com.example.quizprototype.domain.model.QuestionQuery
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.StudyMode
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val dashboard: DashboardSummary? = null,
    val categories: List<Category> = emptyList(),
    val isStarting: Boolean = false,
    val message: String? = null
)

sealed interface HomeEvent {
    data class OpenSession(val sessionId: Long) : HomeEvent
}

class HomeViewModel(
    questionBankRepository: QuestionBankRepository,
    progressRepository: ProgressRepository,
    userProfileRepository: UserProfileRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val transientMessage = MutableStateFlow<String?>(null)
    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        questionBankRepository.observeCategories(),
        progressRepository.observeDashboardSummary(),
        userProfileRepository.observeUserProfile(),
        transientMessage
    ) { categories, dashboard, userProfile, message ->
        HomeUiState(
            userProfile = userProfile,
            dashboard = dashboard,
            categories = categories,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun clearMessage() {
        transientMessage.value = null
    }

    fun startWeakQuestionsSession() {
        viewModelScope.launch {
            runCatching {
                studySessionRepository.startSession(
                    SessionConfig(
                        mode = StudyMode.WEAK_QUESTIONS,
                        title = "Weak Question Review",
                        query = QuestionQuery(weakOnly = true, limit = 10),
                        questionLimit = 10,
                        durationLimitSeconds = null,
                        immediateFeedback = true,
                        allowReviewBeforeSubmit = true
                    )
                )
            }.onSuccess { sessionId ->
                _events.emit(HomeEvent.OpenSession(sessionId))
            }.onFailure {
                transientMessage.value = "No weak questions available yet. Finish a few sessions first."
            }
        }
    }

    companion object {
        fun provideFactory(
            questionBankRepository: QuestionBankRepository,
            progressRepository: ProgressRepository,
            userProfileRepository: UserProfileRepository,
            studySessionRepository: StudySessionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(
                        questionBankRepository = questionBankRepository,
                        progressRepository = progressRepository,
                        userProfileRepository = userProfileRepository,
                        studySessionRepository = studySessionRepository
                    ) as T
                }
            }
    }
}
