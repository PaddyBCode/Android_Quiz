package com.example.quizprototype.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizprototype.data.local.entity.AchievementUnlockEntity
import com.example.quizprototype.domain.model.BadgeId
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_unlocks")
    fun observeUnlocks(): Flow<List<AchievementUnlockEntity>>

    @Query("SELECT badgeId FROM achievement_unlocks")
    suspend fun getUnlockedBadgeIds(): List<BadgeId>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUnlock(unlock: AchievementUnlockEntity)

    @Query("DELETE FROM achievement_unlocks")
    suspend fun deleteAllUnlocks()
}
