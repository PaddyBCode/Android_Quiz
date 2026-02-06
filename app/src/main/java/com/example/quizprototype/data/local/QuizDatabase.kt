package com.example.quizprototype.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quizprototype.data.local.dao.QuizDao
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.QuizEntity

@Database(
    entities = [QuizEntity::class, QuestionEntity::class, AnswerOptionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(QuestionCategoryConverter::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var Instance: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
