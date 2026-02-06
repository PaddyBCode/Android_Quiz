package com.example.quizprototype.data.local

import androidx.room.TypeConverter
import com.example.quizprototype.domain.model.QuestionCategory

class QuestionCategoryConverter {
    @TypeConverter
    fun fromCategory(category: QuestionCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): QuestionCategory = QuestionCategory.valueOf(value)
}
