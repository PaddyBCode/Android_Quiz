package com.example.quizprototype.data.repository

import android.content.res.AssetManager
import com.example.quizprototype.data.content.BundledQuestionPackParser
import com.example.quizprototype.data.content.BundledQuestionPackValidator
import com.example.quizprototype.data.local.dao.ContentDao

class DefaultContentImportRepository(
    private val assetManager: AssetManager,
    private val contentDao: ContentDao,
    private val parser: BundledQuestionPackParser,
    private val validator: BundledQuestionPackValidator,
    private val analyticsLogger: AnalyticsLogger
) : ContentImportRepository {

    override suspend fun ensureBundledContent() {
        val rawJson = assetManager.open(BUNDLED_CONTENT_ASSET_PATH).bufferedReader().use { it.readText() }
        val questionPack = try {
            parser.parse(rawJson).also(validator::validate)
        } catch (throwable: Throwable) {
            analyticsLogger.logError("content_import_failed", throwable)
            throw IllegalStateException(
                "Bundled content validation failed for $BUNDLED_CONTENT_ASSET_PATH: ${throwable.message}",
                throwable
            )
        }
        val currentVersion = contentDao.getContentVersion()
        val currentQuestionCount = contentDao.getQuestionCount()
        val shouldImport = currentVersion?.version != questionPack.contentVersion.version ||
            currentQuestionCount != questionPack.contentVersion.questionCount
        if (!shouldImport) {
            analyticsLogger.logEvent(
                name = "content_import_skipped",
                attributes = mapOf("version" to questionPack.contentVersion.version)
            )
            return
        }

        contentDao.replaceAllContent(
            contentVersion = questionPack.contentVersion,
            categories = questionPack.categories,
            topics = questionPack.topics,
            questions = questionPack.questions,
            options = questionPack.options
        )
        analyticsLogger.logEvent(
            name = "content_import_completed",
            attributes = mapOf(
                "version" to questionPack.contentVersion.version,
                "questionCount" to questionPack.contentVersion.questionCount.toString()
            )
        )
    }

    companion object {
        private const val BUNDLED_CONTENT_ASSET_PATH = "content/question_pack_v1.json"
    }
}
