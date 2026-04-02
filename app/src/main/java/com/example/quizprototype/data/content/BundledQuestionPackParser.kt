package com.example.quizprototype.data.content

import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.CategoryEntity
import com.example.quizprototype.data.local.entity.ContentVersionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.TopicEntity
import com.example.quizprototype.domain.model.LicenceType
import org.json.JSONArray
import org.json.JSONObject

class BundledQuestionPackParser {

    fun parse(rawJson: String): BundledQuestionPack {
        val root = JSONObject(rawJson)
        val version = root.getString("version")
        val licenceTypes = root.getJSONArray("licenceTypes")
        require(licenceTypes.length() > 0) { "Question pack must define at least one licence type." }

        val categories = mutableListOf<CategoryEntity>()
        val topics = mutableListOf<TopicEntity>()
        val questions = mutableListOf<QuestionEntity>()
        val options = mutableListOf<AnswerOptionEntity>()
        val categoryIds = linkedSetOf<String>()
        val topicIds = linkedSetOf<String>()
        val questionIds = linkedSetOf<String>()
        val optionIds = linkedSetOf<String>()

        for (licenceIndex in 0 until licenceTypes.length()) {
            val licenceObject = licenceTypes.getJSONObject(licenceIndex)
            val licenceType = LicenceType.valueOf(licenceObject.getString("id"))
            licenceObject.getJSONArray("categories").forEachIndexed { categoryIndex, categoryValue ->
                val categoryObject = categoryValue as JSONObject
                val categoryId = categoryObject.getString("id")
                require(categoryIds.add(categoryId)) { "Duplicate category id: $categoryId" }
                categories += CategoryEntity(
                    id = categoryId,
                    licenceType = licenceType,
                    title = categoryObject.getString("title"),
                    description = categoryObject.optString("description"),
                    sortOrder = categoryIndex
                )
                categoryObject.getJSONArray("topics").forEachIndexed { topicIndex, topicValue ->
                    val topicObject = topicValue as JSONObject
                    val topicId = topicObject.getString("id")
                    require(topicIds.add(topicId)) { "Duplicate topic id: $topicId" }
                    topics += TopicEntity(
                        id = topicId,
                        categoryId = categoryId,
                        title = topicObject.getString("title"),
                        description = topicObject.optString("description"),
                        sortOrder = topicIndex
                    )
                }
            }

            licenceObject.getJSONArray("questions").forEachIndexed { questionIndex, questionValue ->
                val questionObject = questionValue as JSONObject
                val questionId = questionObject.getString("id")
                require(questionIds.add(questionId)) { "Duplicate question id: $questionId" }
                val categoryId = questionObject.getString("categoryId")
                val topicId = questionObject.getString("topicId")
                require(categoryIds.contains(categoryId)) { "Unknown category id: $categoryId" }
                require(topicIds.contains(topicId)) { "Unknown topic id: $topicId" }
                val answerOptions = questionObject.getJSONArray("options")
                require(answerOptions.length() >= 2) { "Question $questionId must define at least 2 options." }
                val correctOptionId = questionObject.getString("correctOptionId")
                questions += QuestionEntity(
                    id = questionId,
                    licenceType = licenceType,
                    categoryId = categoryId,
                    topicId = topicId,
                    prompt = questionObject.getString("prompt"),
                    explanation = questionObject.getString("explanation"),
                    sourceReference = questionObject.optString("sourceReference"),
                    assetName = questionObject.optString("assetName").ifBlank { null },
                    isExamEligible = questionObject.optBoolean("examEligible", true),
                    sortOrder = questionIndex
                )
                var sawCorrectOption = false
                answerOptions.forEachIndexed { optionIndex, optionValue ->
                    val optionObject = optionValue as JSONObject
                    val optionId = optionObject.getString("id")
                    require(optionIds.add(optionId)) { "Duplicate option id: $optionId" }
                    if (optionId == correctOptionId) {
                        sawCorrectOption = true
                    }
                    options += AnswerOptionEntity(
                        id = optionId,
                        questionId = questionId,
                        text = optionObject.getString("text"),
                        isCorrect = optionId == correctOptionId,
                        sortOrder = optionIndex
                    )
                }
                require(sawCorrectOption) { "Question $questionId is missing its correct option id." }
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

private inline fun JSONArray.forEachIndexed(action: (Int, Any) -> Unit) {
    for (index in 0 until length()) {
        action(index, get(index))
    }
}
