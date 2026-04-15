package com.example.quizprototype.data.content

import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity
import com.example.quizprototype.domain.model.LicenceType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BundledQuestionPackParser {

    fun parse(rawJson: String): BundledQuestionPack {
        val root = try {
            JSONObject(rawJson)
        } catch (throwable: JSONException) {
            throw ContentValidationException("Root JSON is invalid: ${throwable.message}", throwable)
        }
        val version = root.requiredString("version", "root")
        val licenceTypes = root.requiredArray("licenceTypes", "root")
        requireValidation(licenceTypes.length() > 0) {
            "root.licenceTypes must define at least one licence type."
        }

        val categories = mutableListOf<CategoryEntity>()
        val topics = mutableListOf<TopicEntity>()
        val questions = mutableListOf<QuestionEntity>()
        val options = mutableListOf<AnswerOptionEntity>()

        val categoryIds = linkedSetOf<String>()
        val topicIds = linkedSetOf<String>()
        val questionIds = linkedSetOf<String>()
        val optionIds = linkedSetOf<String>()

        for (licenceIndex in 0 until licenceTypes.length()) {
            val licencePath = "root.licenceTypes[$licenceIndex]"
            val licenceObject = licenceTypes.requiredObject(licenceIndex, licencePath)
            val licenceTypeId = licenceObject.requiredString("id", licencePath)
            val licenceType = try {
                LicenceType.valueOf(licenceTypeId)
            } catch (throwable: IllegalArgumentException) {
                throw ContentValidationException(
                    "$licencePath.id has unsupported value '$licenceTypeId'.",
                    throwable
                )
            }

            val categoriesArray = licenceObject.requiredArray("categories", licencePath)
            requireValidation(categoriesArray.length() > 0) {
                "$licencePath.categories must define at least one category."
            }
            for (categoryIndex in 0 until categoriesArray.length()) {
                val categoryPath = "$licencePath.categories[$categoryIndex]"
                val categoryObject = categoriesArray.requiredObject(categoryIndex, categoryPath)
                val categoryId = categoryObject.requiredString("id", categoryPath)
                requireValidation(categoryIds.add(categoryId)) {
                    "$categoryPath.id duplicates category id '$categoryId'."
                }
                categories += CategoryEntity(
                    id = categoryId,
                    licenceType = licenceType,
                    title = categoryObject.requiredString("title", categoryPath),
                    description = categoryObject.optTrimmedString("description").orEmpty(),
                    sortOrder = categoryIndex
                )

                val topicsArray = categoryObject.requiredArray("topics", categoryPath)
                requireValidation(topicsArray.length() > 0) {
                    "$categoryPath.topics must define at least one topic."
                }
                for (topicIndex in 0 until topicsArray.length()) {
                    val topicPath = "$categoryPath.topics[$topicIndex]"
                    val topicObject = topicsArray.requiredObject(topicIndex, topicPath)
                    val topicId = topicObject.requiredString("id", topicPath)
                    requireValidation(topicIds.add(topicId)) {
                        "$topicPath.id duplicates topic id '$topicId'."
                    }
                    topics += TopicEntity(
                        id = topicId,
                        categoryId = categoryId,
                        title = topicObject.requiredString("title", topicPath),
                        description = topicObject.optTrimmedString("description").orEmpty(),
                        sortOrder = topicIndex
                    )
                }
            }

            val questionsArray = licenceObject.requiredArray("questions", licencePath)
            requireValidation(questionsArray.length() > 0) {
                "$licencePath.questions must define at least one question."
            }
            for (questionIndex in 0 until questionsArray.length()) {
                val questionPath = "$licencePath.questions[$questionIndex]"
                val questionObject = questionsArray.requiredObject(questionIndex, questionPath)
                val questionId = questionObject.requiredString("id", questionPath)
                requireValidation(questionIds.add(questionId)) {
                    "$questionPath.id duplicates question id '$questionId'."
                }

                val categoryId = questionObject.requiredString("categoryId", questionPath)
                requireValidation(categoryIds.contains(categoryId)) {
                    "$questionPath.categoryId references unknown category '$categoryId' for question '$questionId'."
                }
                val topicId = questionObject.requiredString("topicId", questionPath)
                requireValidation(topicIds.contains(topicId)) {
                    "$questionPath.topicId references unknown topic '$topicId' for question '$questionId'."
                }

                val optionsArray = questionObject.requiredArray("options", questionPath)
                requireValidation(optionsArray.length() >= 2) {
                    "$questionPath.options must define at least 2 options for question '$questionId'."
                }
                val correctOptionId = questionObject.requiredString("correctOptionId", questionPath)
                val assetName = questionObject.optTrimmedString("assetName")

                questions += QuestionEntity(
                    id = questionId,
                    licenceType = licenceType,
                    categoryId = categoryId,
                    topicId = topicId,
                    prompt = questionObject.requiredString("prompt", questionPath),
                    explanation = questionObject.requiredString("explanation", questionPath),
                    sourceReference = questionObject.requiredString("sourceReference", questionPath),
                    assetName = assetName,
                    isExamEligible = questionObject.optBoolean("examEligible", true),
                    sortOrder = questionIndex
                )

                var sawCorrectOption = false
                for (optionIndex in 0 until optionsArray.length()) {
                    val optionPath = "$questionPath.options[$optionIndex]"
                    val optionObject = optionsArray.requiredObject(optionIndex, optionPath)
                    val optionId = optionObject.requiredString("id", optionPath)
                    requireValidation(optionIds.add(optionId)) {
                        "$optionPath.id duplicates option id '$optionId'."
                    }
                    if (optionId == correctOptionId) {
                        sawCorrectOption = true
                    }
                    options += AnswerOptionEntity(
                        id = optionId,
                        questionId = questionId,
                        text = optionObject.requiredString("text", optionPath),
                        isCorrect = optionId == correctOptionId,
                        sortOrder = optionIndex
                    )
                }
                requireValidation(sawCorrectOption) {
                    "$questionPath.correctOptionId references missing option '$correctOptionId' for question '$questionId'."
                }
            }
        }

        return BundledQuestionPack(
            contentVersion = ContentVersionEntity(
                version = version,
                importedAtEpochMillis = System.currentTimeMillis(),
                questionCount = questions.size
            ),
            categories = categories,
            topics = topics,
            questions = questions,
            options = options
        )
    }
}

class ContentValidationException(
    message: String,
    cause: Throwable? = null
) : IllegalArgumentException(message, cause)

private fun requireValidation(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        throw ContentValidationException(lazyMessage())
    }
}

private fun JSONObject.requiredString(fieldName: String, path: String): String {
    if (!has(fieldName)) {
        throw ContentValidationException("$path.$fieldName is required.")
    }
    val value = optString(fieldName, "").trim()
    if (value.isBlank()) {
        throw ContentValidationException("$path.$fieldName must not be blank.")
    }
    return value
}

private fun JSONObject.optTrimmedString(fieldName: String): String? {
    if (!has(fieldName)) return null
    return optString(fieldName).trim().ifBlank { null }
}

private fun JSONObject.requiredArray(fieldName: String, path: String): JSONArray {
    return try {
        getJSONArray(fieldName)
    } catch (throwable: JSONException) {
        throw ContentValidationException("$path.$fieldName must be an array.", throwable)
    }
}

private fun JSONArray.requiredObject(index: Int, path: String): JSONObject {
    return try {
        getJSONObject(index)
    } catch (throwable: JSONException) {
        throw ContentValidationException("$path must be an object.", throwable)
    }
}
