package com.example.quizprototype.data.seed

import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.QuizEntity
import com.example.quizprototype.domain.model.QuestionCategory

object SampleQuizData {
    private const val QUIZ_ID = 1

    val quiz = QuizEntity(
        id = QUIZ_ID,
        title = "Ireland Driver Theory - Starter Quiz",
        description = "Sample multiple-choice questions seeded from local data."
    )

    val questions = listOf(
        QuestionEntity(
            id = 101,
            quizId = QUIZ_ID,
            prompt = "What should you do when approaching a yellow box junction that is occupied?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            orderIndex = 0
        ),
        QuestionEntity(
            id = 102,
            quizId = QUIZ_ID,
            prompt = "What is the safest way to use a mobile phone while driving?",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            orderIndex = 1
        ),
        QuestionEntity(
            id = 103,
            quizId = QUIZ_ID,
            prompt = "What should you do before changing lanes on a dual carriageway?",
            category = QuestionCategory.MANAGING_RISK,
            orderIndex = 2
        ),
        QuestionEntity(
            id = 104,
            quizId = QUIZ_ID,
            prompt = "When can you overtake on the left in slow moving traffic?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            orderIndex = 3
        ),
        QuestionEntity(
            id = 105,
            quizId = QUIZ_ID,
            prompt = "What does a continuous white line along the center of the road mean?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            orderIndex = 4
        )
    )

    val options = listOf(
        AnswerOptionEntity(1001, 101, "Enter if you can clear it quickly", false, 0),
        AnswerOptionEntity(1002, 101, "Wait until your exit is clear", true, 1),
        AnswerOptionEntity(1003, 101, "Drive around vehicles in the junction", false, 2),
        AnswerOptionEntity(1004, 101, "Use your horn and continue", false, 3),

        AnswerOptionEntity(1005, 102, "Hold it below window level", false, 0),
        AnswerOptionEntity(1006, 102, "Use speaker mode when traffic is light", false, 1),
        AnswerOptionEntity(1007, 102, "Only use when fully stopped in a safe place", true, 2),
        AnswerOptionEntity(1008, 102, "Text only at red traffic lights", false, 3),

        AnswerOptionEntity(1009, 103, "Signal, mirror, blind spot check, then move if safe", true, 0),
        AnswerOptionEntity(1010, 103, "Move first, then signal your intent", false, 1),
        AnswerOptionEntity(1011, 103, "Check your rear-view mirror only", false, 2),
        AnswerOptionEntity(1012, 103, "Increase speed without checking mirrors", false, 3),

        AnswerOptionEntity(1013, 104, "Never, under any circumstances", false, 0),
        AnswerOptionEntity(1014, 104, "When traffic in your lane is moving faster", true, 1),
        AnswerOptionEntity(1015, 104, "Only on motorways at all times", false, 2),
        AnswerOptionEntity(1016, 104, "Whenever the right lane is busy", false, 3),

        AnswerOptionEntity(1017, 105, "You may cross it to overtake when clear", false, 0),
        AnswerOptionEntity(1018, 105, "Do not cross or straddle except where permitted", true, 1),
        AnswerOptionEntity(1019, 105, "Parking is allowed on either side", false, 2),
        AnswerOptionEntity(1020, 105, "Traffic has priority from the left", false, 3)
    )
}
