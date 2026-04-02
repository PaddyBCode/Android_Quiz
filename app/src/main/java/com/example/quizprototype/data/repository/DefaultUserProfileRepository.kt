package com.example.quizprototype.data.repository

import com.example.quizprototype.data.local.dao.UserProfileDao
import com.example.quizprototype.data.local.entity.UserProfileEntity
import com.example.quizprototype.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultUserProfileRepository(
    private val userProfileDao: UserProfileDao,
    private val analyticsLogger: AnalyticsLogger
) : UserProfileRepository {

    override fun observeUserProfile(): Flow<UserProfile?> {
        return userProfileDao.observeUserProfile().map { profile ->
            profile?.toDomain()
        }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()?.toDomain()
    }

    override suspend fun createProfile(username: String) {
        val trimmedUsername = username.trim()
        require(trimmedUsername.isNotBlank()) { "Please enter a username." }
        require(trimmedUsername.length >= 2) { "Username must be at least 2 characters." }
        userProfileDao.upsertUserProfile(
            UserProfileEntity(
                username = trimmedUsername,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
        analyticsLogger.logEvent(
            "profile_created",
            mapOf("usernameLength" to trimmedUsername.length.toString())
        )
    }
}

private fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        username = username,
        createdAtEpochMillis = createdAtEpochMillis
    )
}
