package com.example.quizprototype.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.QuestionBankRepository
import com.example.quizprototype.domain.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ReviewPickerUiState(
    val categories: List<Category> = emptyList()
)

class ReviewPickerViewModel(
    questionBankRepository: QuestionBankRepository
) : ViewModel() {

    val uiState: StateFlow<ReviewPickerUiState> = questionBankRepository.observeCategories()
        .map { categories -> ReviewPickerUiState(categories = categories) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReviewPickerUiState()
        )

    companion object {
        fun provideFactory(
            questionBankRepository: QuestionBankRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReviewPickerViewModel(questionBankRepository) as T
                }
            }
    }
}
