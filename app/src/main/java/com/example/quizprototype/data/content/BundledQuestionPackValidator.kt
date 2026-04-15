package com.example.quizprototype.data.content

class BundledQuestionPackValidator(
    private val assetExists: (String) -> Boolean,
    private val drawableExists: (String) -> Boolean
) {

    fun validate(questionPack: BundledQuestionPack) {
        questionPack.questions.forEach { question ->
            val assetName = question.assetName ?: return@forEach
            val isValid = if (assetName.contains("/") || assetName.contains(".")) {
                assetExists(assetName)
            } else {
                drawableExists(assetName)
            }
            if (!isValid) {
                val referenceType = if (assetName.contains("/") || assetName.contains(".")) {
                    "asset file"
                } else {
                    "drawable resource"
                }
                throw ContentValidationException(
                    "Question '${question.id}' has invalid assetName '$assetName': $referenceType not found."
                )
            }
        }
    }
}
