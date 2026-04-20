package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizprototype.data.local.entity.UserProfileEntity
import com.example.quizprototype.domain.model.AppThemeMode
import com.example.quizprototype.domain.model.ProfileAvatarId
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET themeMode = :themeMode WHERE id = 1")
    suspend fun updateThemeMode(themeMode: AppThemeMode)

    @Query("UPDATE user_profiles SET avatarId = :avatarId WHERE id = 1")
    suspend fun updateAvatarId(avatarId: ProfileAvatarId)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteUserProfile()
}
