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
        private const val DATABASE_NAME = "quiz_database"

        @Volatile
        private var Instance: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: buildDatabaseWithPrototypeReset(context.applicationContext)
                    .also { Instance = it }
            }
        }

        private fun buildDatabase(context: Context): QuizDatabase {
            return Room.databaseBuilder(
                context,
                QuizDatabase::class.java,
                DATABASE_NAME
            ).build()
        }

        private fun buildDatabaseWithPrototypeReset(context: Context): QuizDatabase {
            var candidate: QuizDatabase? = null
            return runCatching {
                candidate = buildDatabase(context)
                // Force open now so schema issues are handled once during startup.
                candidate?.openHelper?.writableDatabase
                candidate!!
            }.getOrElse {
                candidate?.close()
                context.deleteDatabase(DATABASE_NAME)
                buildDatabase(context)
            }
        }
    }
}
