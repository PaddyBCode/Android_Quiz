package com.example.quizprototype.data.content

import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.domain.model.LicenceType
import org.junit.Assert.assertTrue
import org.junit.Test

class BundledQuestionPackValidatorTest {

    @Test
    fun `validate accepts known drawable asset names`() {
        val validator = BundledQuestionPackValidator(
            assetExists = { false },
            drawableExists = { it == "sign_stop" }
        )

        validator.validate(questionPackWithAsset("sign_stop"))
    }

    @Test
    fun `validate accepts known asset file paths`() {
        val validator = BundledQuestionPackValidator(
            assetExists = { it == "content/images/sign_stop.png" },
            drawableExists = { false }
        )

        validator.validate(questionPackWithAsset("content/images/sign_stop.png"))
    }

    @Test
    fun `validate reports exact question id for missing image reference`() {
        val validator = BundledQuestionPackValidator(
            assetExists = { false },
            drawableExists = { false }
        )

        val throwable = org.junit.Assert.assertThrows(ContentValidationException::class.java) {
            validator.validate(questionPackWithAsset("missing_sign"))
        }

        assertTrue(throwable.message!!.contains("Question 'q_image'"))
        assertTrue(throwable.message!!.contains("missing_sign"))
    }

    private fun questionPackWithAsset(assetName: String): BundledQuestionPack {
        return BundledQuestionPack(
            contentVersion = ContentVersionEntity(
                version = "1.0.0",
                importedAtEpochMillis = 0L,
                questionCount = 1
            ),
            categories = emptyList(),
            topics = emptyList(),
            questions = listOf(
                QuestionEntity(
                    id = "q_image",
                    licenceType = LicenceType.CAR,
                    categoryId = "rules",
                    topicId = "signals",
                    prompt = "Image question",
                    explanation = "Explanation",
                    sourceReference = "Rules",
                    assetName = assetName,
                    isExamEligible = true,
                    sortOrder = 0
                )
            ),
            options = emptyList()
        )
    }
}
