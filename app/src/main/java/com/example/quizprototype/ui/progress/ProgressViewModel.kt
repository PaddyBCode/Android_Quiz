package com.example.quizprototype.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.ProgressRepository
import com.example.quizprototype.domain.model.ProgressSnapshot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<ProgressSnapshot> = progressRepository.observeProgressSnapshot().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressSnapshot(
            completedSessions = 0,
            totalAnsweredQuestions = 0,
            correctAnswers = 0,
            averageScorePercent = 0,
            strongestCategories = emptyList(),
            weakestCategories = emptyList(),
            recentResults = emptyList()
        )
    )

    companion object {
        fun provideFactory(progressRepository: ProgressRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProgressViewModel(progressRepository) as T
                }
            }
    }
}
