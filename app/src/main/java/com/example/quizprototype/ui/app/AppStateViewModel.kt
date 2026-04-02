package com.example.quizprototype.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.ContentImportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppStateUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class AppStateViewModel(
    private val contentImportRepository: ContentImportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppStateUiState())
    val uiState: StateFlow<AppStateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                contentImportRepository.ensureBundledContent()
            }.onSuccess {
                _uiState.value = AppStateUiState(isLoading = false)
            }.onFailure { throwable ->
                _uiState.value = AppStateUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Failed to import bundled content."
                )
            }
        }
    }

    companion object {
        fun provideFactory(contentImportRepository: ContentImportRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppStateViewModel(contentImportRepository) as T
                }
            }
    }
}
