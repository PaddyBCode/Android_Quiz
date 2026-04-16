package com.example.quizprototype.data.local

import androidx.room.TypeConverter
import com.example.quizprototype.domain.model.BadgeId
import com.example.quizprototype.domain.model.LicenceType
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
}
