package com.example.quizprototype.data.local

import androidx.room.TypeConverter
import com.example.quizprototype.domain.model.AppThemeMode
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.domain.model.LicenceType
import com.example.quizprototype.domain.model.ProfileAvatarId
import com.example.quizprototype.domain.model.StudyMode

class AppTypeConverters {
    @TypeConverter
    fun fromLicenceType(value: LicenceType): String = value.name

    @TypeConverter
    fun toLicenceType(value: String): LicenceType = LicenceType.valueOf(value)

    @TypeConverter
    fun fromStudyMode(value: StudyMode): String = value.name

    @TypeConverter
    fun toStudyMode(value: String): StudyMode = StudyMode.valueOf(value)

    @TypeConverter
    fun fromBadgeId(value: BadgeId): String = value.name

    @TypeConverter
    fun toBadgeId(value: String): BadgeId = BadgeId.valueOf(value)

    @TypeConverter
    fun fromAppThemeMode(value: AppThemeMode): String = value.name

    @TypeConverter
    fun toAppThemeMode(value: String): AppThemeMode = AppThemeMode.valueOf(value)

    @TypeConverter
    fun fromProfileAvatarId(value: ProfileAvatarId): String = value.name

    @TypeConverter
    fun toProfileAvatarId(value: String): ProfileAvatarId = ProfileAvatarId.valueOf(value)
}
