package com.example.quizprototype.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val username: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

sealed interface OnboardingEvent {
    data object ProfileCreated : OnboardingEvent
}

class OnboardingViewModel(
    initialUsername: String,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val username = MutableStateFlow(initialUsername)
    private val isSaving = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<OnboardingUiState> = combine(
        username,
        isSaving,
        errorMessage
    ) { currentUsername, saving, error ->
        OnboardingUiState(
            username = currentUsername,
            isSaving = saving,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OnboardingUiState(username = initialUsername)
    )

    fun onUsernameChanged(value: String) {
        username.value = value
        errorMessage.value = null
    }

    fun createProfile() {
        viewModelScope.launch {
            isSaving.value = true
            runCatching {
                userProfileRepository.createProfile(username.value)
            }.onSuccess {
                _events.emit(OnboardingEvent.ProfileCreated)
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to create profile."
            }
            isSaving.value = false
        }
    }

    companion object {
        fun provideFactory(
            initialUsername: String,
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OnboardingViewModel(initialUsername, userProfileRepository) as T
                }
            }
    }
}
