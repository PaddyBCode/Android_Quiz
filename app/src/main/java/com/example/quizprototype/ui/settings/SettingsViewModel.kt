package com.example.quizprototype.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isResetting: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SettingsEvent {
    data class ProfileReset(val previousUsername: String?) : SettingsEvent
}

class SettingsViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetProfile() {
        if (_uiState.value.isResetting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isResetting = true, errorMessage = null)
            runCatching {
                userProfileRepository.resetProfile()
            }.onSuccess { previousUsername ->
                _uiState.value = _uiState.value.copy(isResetting = false)
                _events.emit(SettingsEvent.ProfileReset(previousUsername))
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    isResetting = false,
                    errorMessage = throwable.message ?: "Unable to reset profile."
                )
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
