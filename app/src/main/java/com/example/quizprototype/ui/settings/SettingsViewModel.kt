package com.example.quizprototype.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.UserProfileRepository
import com.example.quizprototype.domain.model.AppThemeMode
import com.example.quizprototype.domain.model.ProfileAvatarId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val isUpdatingTheme: Boolean = false,
    val avatarId: ProfileAvatarId = ProfileAvatarId.WOMAN_DOG,
    val isUpdatingAvatar: Boolean = false,
    val isResetting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SettingsEvent {
    data class ProfileReset(val previousUsername: String?) : SettingsEvent
}

class SettingsViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val isUpdatingTheme = MutableStateFlow(false)
    private val isUpdatingAvatar = MutableStateFlow(false)
    private val isResetting = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        userProfileRepository.observeUserProfile(),
        isUpdatingTheme,
        isUpdatingAvatar,
        isResetting,
        errorMessage
    ) { userProfile, updatingTheme, updatingAvatar, resetting, error ->
        SettingsUiState(
            themeMode = userProfile?.themeMode ?: AppThemeMode.DARK,
            avatarId = userProfile?.avatarId ?: ProfileAvatarId.WOMAN_DOG,
            isUpdatingTheme = updatingTheme,
            isUpdatingAvatar = updatingAvatar,
            isResetting = resetting,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    fun clearError() {
        errorMessage.value = null
    }

    fun updateThemeMode(themeMode: AppThemeMode) {
        if (isUpdatingTheme.value || uiState.value.themeMode == themeMode) return
        viewModelScope.launch {
            isUpdatingTheme.value = true
            errorMessage.value = null
            runCatching {
                userProfileRepository.updateThemeMode(themeMode)
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to update the theme."
            }
            isUpdatingTheme.value = false
        }
    }

    fun updateProfileAvatar(avatarId: ProfileAvatarId) {
        if (isUpdatingAvatar.value || uiState.value.avatarId == avatarId) return
        viewModelScope.launch {
            isUpdatingAvatar.value = true
            errorMessage.value = null
            runCatching {
                userProfileRepository.updateProfileAvatar(avatarId)
            }.onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Unable to update the profile picture."
            }
            isUpdatingAvatar.value = false
        }
    }

    fun resetProfile() {
        if (isResetting.value) return
        viewModelScope.launch {
            isResetting.value = true
            errorMessage.value = null
            runCatching {
                userProfileRepository.resetProfile()
            }.onSuccess { previousUsername ->
                isResetting.value = false
                _events.emit(SettingsEvent.ProfileReset(previousUsername))
            }.onFailure { throwable ->
                isResetting.value = false
                errorMessage.value = throwable.message ?: "Unable to reset profile."
            }
        }
    }

    companion object {
        fun provideFactory(
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(userProfileRepository) as T
                }
            }
    }
}
