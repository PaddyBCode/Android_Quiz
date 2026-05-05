package com.example.quizprototype.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.AchievementsRepository
import com.example.quizprototype.data.repository.ProgressRepository
import com.example.quizprototype.data.repository.UserProfileRepository
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.domain.model.DashboardSummary
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UserUiState(
    val userProfile: UserProfile? = null,
    val dashboard: DashboardSummary? = null,
    val recentUnlockedAchievements: List<AchievementBadge> = emptyList()
)

class UserViewModel(
    userProfileRepository: UserProfileRepository,
    progressRepository: ProgressRepository,
    achievementsRepository: AchievementsRepository
) : ViewModel() {

    val uiState: StateFlow<UserUiState> = combine(
        userProfileRepository.observeUserProfile(),
        progressRepository.observeDashboardSummary(),
        achievementsRepository.observeBadges()
    ) { userProfile, dashboard, badges ->
        UserUiState(
            userProfile = userProfile,
            dashboard = dashboard,
            recentUnlockedAchievements = badges
                .filter { it.unlocked }
                .sortedByDescending { it.unlockedAtEpochMillis ?: Long.MIN_VALUE }
                .take(4)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserUiState()
    )

    companion object {
        fun provideFactory(
            userProfileRepository: UserProfileRepository,
            progressRepository: ProgressRepository,
            achievementsRepository: AchievementsRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(
                        userProfileRepository = userProfileRepository,
                        progressRepository = progressRepository,
                        achievementsRepository = achievementsRepository
                    ) as T
                }
            }
    }
}
