package com.example.quizprototype.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.AchievementsRepository
import com.example.quizprototype.data.repository.UserProfileRepository
import com.example.quizprototype.domain.model.AchievementBadge
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AchievementsUiState(
    val userProfile: UserProfile? = null,
    val badges: List<AchievementBadge> = emptyList()
) {
    val unlockedCount: Int
        get() = badges.count { it.unlocked }
}

class AchievementsViewModel(
    achievementsRepository: AchievementsRepository,
    userProfileRepository: UserProfileRepository
) : ViewModel() {

    val uiState: StateFlow<AchievementsUiState> = combine(
        achievementsRepository.observeBadges(),
        userProfileRepository.observeUserProfile()
    ) { badges, userProfile ->
        AchievementsUiState(
            userProfile = userProfile,
            badges = badges
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AchievementsUiState()
    )

    companion object {
        fun provideFactory(
            achievementsRepository: AchievementsRepository,
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AchievementsViewModel(
                        achievementsRepository = achievementsRepository,
                        userProfileRepository = userProfileRepository
                    ) as T
                }
            }
    }
}
