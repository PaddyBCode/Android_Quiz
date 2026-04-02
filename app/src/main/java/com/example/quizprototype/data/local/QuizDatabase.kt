package com.example.quizprototype.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quizprototype.data.local.dao.BookmarkDao
import com.example.quizprototype.data.local.dao.ContentDao
import com.example.quizprototype.data.local.dao.QuestionBankDao
import com.example.quizprototype.data.local.dao.StudySessionDao
import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.AnswerRecordEntity
import com.example.quizprototype.data.local.entity.BookmarkEntity
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.StudySessionEntity
import com.example.quizprototype.data.local.entity.StudySessionQuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity

@Database(
    entities = [
        CategoryEntity::class,
        TopicEntity::class,
        QuestionEntity::class,
        AnswerOptionEntity::class,
        ContentVersionEntity::class,
        BookmarkEntity::class,
        StudySessionEntity::class,
        StudySessionQuestionEntity::class,
        AnswerRecordEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(AppTypeConverters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    abstract fun questionBankDao(): QuestionBankDao

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun studySessionDao(): StudySessionDao

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
            )
                .fallbackToDestructiveMigration(true)
                .build()
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
