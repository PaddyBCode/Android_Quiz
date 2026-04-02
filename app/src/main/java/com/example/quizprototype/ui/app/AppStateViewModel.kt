package com.example.quizprototype.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.ContentImportRepository
import com.example.quizprototype.data.repository.UserProfileRepository
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AppStateUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val userProfile: UserProfile? = null
)

class AppStateViewModel(
    private val contentImportRepository: ContentImportRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStateUiState())
    val uiState: StateFlow<AppStateUiState> = _uiState.asStateFlow()
    private var contentReady = false
    private var profileResolved = false

    init {
        viewModelScope.launch {
            runCatching {
                contentImportRepository.ensureBundledContent()
            }.onSuccess {
                contentReady = true
                _uiState.value = _uiState.value.copy(isLoading = !profileResolved)
            }.onFailure { throwable ->
                _uiState.value = AppStateUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Failed to import bundled content."
                )
            }
        }

        viewModelScope.launch {
            userProfileRepository.observeUserProfile().collectLatest { profile ->
                profileResolved = true
                if (_uiState.value.errorMessage == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = !(contentReady && profileResolved),
                        userProfile = profile
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            contentImportRepository: ContentImportRepository,
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppStateViewModel(contentImportRepository, userProfileRepository) as T
                }
            }
    }
}
