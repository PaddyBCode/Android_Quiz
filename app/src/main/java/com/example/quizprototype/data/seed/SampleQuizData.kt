package com.example.quizprototype.data.seed

import com.example.quizprototype.data.local.entity.AnswerOptionEntity
import com.example.quizprototype.data.local.entity.QuestionEntity
import com.example.quizprototype.data.local.entity.QuizEntity
import com.example.quizprototype.domain.model.QuestionCategory

object SampleQuizData {
    private const val QUIZ_ID = 1
    private const val FIRST_QUESTION_ID = 101
    private const val FIRST_OPTION_ID = 1001
    private const val OPTIONS_PER_QUESTION = 4

    val quiz = QuizEntity(
        id = QUIZ_ID,
        title = "Ireland Driver Theory - Starter Quiz",
        description = "Sample multiple-choice questions across all theory categories."
    )

    private val seedQuestions = listOf(
        SeedQuestion(
            prompt = "What steering technique gives best control in normal driving?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            options = listOf(
                "Cross your arms quickly while turning",
                "Use push-pull steering with both hands",
                "Steer mainly with one hand",
                "Hold the wheel from inside the rim"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "When driving downhill for a long distance, what helps keep control?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            options = listOf(
                "Stay in a higher gear and brake hard",
                "Select a lower gear to use engine braking",
                "Switch to neutral to roll smoothly",
                "Use the handbrake lightly"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "If your car starts to skid, what should you do first?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            options = listOf(
                "Brake as hard as possible",
                "Accelerate sharply",
                "Ease off accelerator and steer gently into the skid",
                "Pull the handbrake"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "What is the best way to brake for a smooth stop?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            options = listOf(
                "Brake progressively and gently release near stop",
                "Brake late and firmly",
                "Pump the brake repeatedly",
                "Use handbrake before foot brake"
            ),
            correctIndex = 0
        ),
        SeedQuestion(
            prompt = "Before moving off from the roadside, what is most important?",
            category = QuestionCategory.CONTROL_OF_VEHICLE,
            options = listOf(
                "Rev engine and move quickly",
                "Check mirrors and blind spot, then move when safe",
                "Move immediately with indicator on",
                "Sound the horn and pull out"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "What should you do when a yellow box junction is occupied?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            options = listOf(
                "Enter if lights are green",
                "Wait until your exit is clear",
                "Enter slowly and stop in the box",
                "Use horn and continue"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "What does a continuous white center line generally mean?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            options = listOf(
                "Overtaking is encouraged",
                "Do not cross or straddle unless permitted",
                "Parking is allowed at all times",
                "Traffic from left has priority"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "When can you overtake on the left in slow traffic?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            options = listOf(
                "Never in any situation",
                "Whenever you are in a hurry",
                "When traffic in your lane is moving faster",
                "Only on hard shoulders"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "What should you do at an amber traffic light if safe to stop?",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            options = listOf(
                "Speed up and clear junction",
                "Stop before the line",
                "Continue if no camera is visible",
                "Stop only if pedestrians are crossing"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "A circular sign with red border and 50 means:",
            category = QuestionCategory.LEGAL_MATTERS_RULES_OF_THE_ROAD,
            options = listOf(
                "Recommended speed 50 km/h",
                "Minimum speed 50 km/h",
                "Speed limit 50 km/h",
                "Road number 50"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "Before changing lanes, what sequence is safest?",
            category = QuestionCategory.MANAGING_RISK,
            options = listOf(
                "Signal, mirror, blind spot check, move if safe",
                "Signal and move immediately",
                "Check center mirror only",
                "Increase speed, then check mirrors"
            ),
            correctIndex = 0
        ),
        SeedQuestion(
            prompt = "How should you adjust following distance in heavy rain?",
            category = QuestionCategory.MANAGING_RISK,
            options = listOf(
                "Keep the same gap as dry weather",
                "Reduce gap to prevent cut-ins",
                "Increase gap to allow longer stopping distance",
                "Drive close and brake early"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "Approaching a blind bend, what is the safest action?",
            category = QuestionCategory.MANAGING_RISK,
            options = listOf(
                "Move to center for better view",
                "Reduce speed and keep proper lane position",
                "Overtake before entering the bend",
                "Use full beam to warn others"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "If you start overtaking and see oncoming traffic closing fast:",
            category = QuestionCategory.MANAGING_RISK,
            options = listOf(
                "Accelerate and force completion",
                "Brake hard in opposing lane",
                "Return safely to your lane as soon as possible",
                "Flash headlights and continue"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "At night, when should you dip your headlights?",
            category = QuestionCategory.MANAGING_RISK,
            options = listOf(
                "Only in towns",
                "When following or meeting other road users",
                "Only during rain",
                "Never on national roads"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "What is the safest mobile phone policy while driving?",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            options = listOf(
                "Use it at red lights",
                "Use speaker mode in light traffic",
                "Use only when fully stopped in a safe place",
                "Text briefly on straight roads"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "What should you do when an emergency vehicle approaches with siren?",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            options = listOf(
                "Brake suddenly where you are",
                "Move safely aside and allow clear passage",
                "Speed up to stay ahead",
                "Block junction to prevent others moving"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "Approaching a pedestrian crossing with people waiting, you should:",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            options = listOf(
                "Continue if they have not stepped out",
                "Slow down and be prepared to stop",
                "Sound horn to make them wait",
                "Pass quickly before they cross"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "If you feel very tired while driving, what should you do?",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            options = listOf(
                "Open window and keep driving",
                "Drink coffee and continue without stopping",
                "Stop in a safe place and rest",
                "Drive faster to reach destination sooner"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "When passing a cyclist, what is best practice?",
            category = QuestionCategory.SAFE_AND_SOCIAL_RESPONSIBLE_DRIVING,
            options = listOf(
                "Pass close to discourage wobble",
                "Leave as much safe space as possible",
                "Use horn while overtaking",
                "Pass only if cyclist signals you"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "A red brake warning light stays on while driving. You should:",
            category = QuestionCategory.TECHNICAL_MATTERS,
            options = listOf(
                "Ignore until next service",
                "Drive faster to clear it",
                "Stop safely and investigate immediately",
                "Turn headlights off and on"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "What can under-inflated tyres cause?",
            category = QuestionCategory.TECHNICAL_MATTERS,
            options = listOf(
                "Better fuel economy",
                "Improved cornering",
                "Poor handling and longer braking distance",
                "No change in vehicle behavior"
            ),
            correctIndex = 2
        ),
        SeedQuestion(
            prompt = "Why should tyre tread depth be checked regularly?",
            category = QuestionCategory.TECHNICAL_MATTERS,
            options = listOf(
                "To improve stereo performance",
                "To maintain grip, especially in wet conditions",
                "To reduce steering wheel size",
                "To increase engine power"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "What is the main purpose of ABS brakes?",
            category = QuestionCategory.TECHNICAL_MATTERS,
            options = listOf(
                "Reduce fuel usage",
                "Prevent wheel lock during hard braking",
                "Increase top speed",
                "Shorten stopping distance on every surface"
            ),
            correctIndex = 1
        ),
        SeedQuestion(
            prompt = "If the engine temperature warning shows overheating, you should:",
            category = QuestionCategory.TECHNICAL_MATTERS,
            options = listOf(
                "Keep driving to cool with airflow",
                "Stop safely and switch off engine when appropriate",
                "Pour cold water into hot engine immediately",
                "Ignore it if car still moves"
            ),
            correctIndex = 1
        )
    )

    val questions = seedQuestions.mapIndexed { index, seed ->
        QuestionEntity(
            id = FIRST_QUESTION_ID + index,
            quizId = QUIZ_ID,
            prompt = seed.prompt,
            category = seed.category,
            orderIndex = index
        )
    }

    val options = seedQuestions.flatMapIndexed { questionIndex, seed ->
        val questionId = FIRST_QUESTION_ID + questionIndex
        seed.options.mapIndexed { optionIndex, text ->
            AnswerOptionEntity(
                id = FIRST_OPTION_ID + (questionIndex * OPTIONS_PER_QUESTION) + optionIndex,
                questionId = questionId,
                text = text,
                isCorrect = optionIndex == seed.correctIndex,
                orderIndex = optionIndex
            )
        }
    }

    init {
        require(seedQuestions.size == 25) { "Seed quiz should contain 25 questions." }
       }
}

private data class SeedQuestion(
    val prompt: String,
    val category: QuestionCategory,
    val options: List<String>,
    val correctIndex: Int
) {
    init {
        require(options.size == 4) { "Each question must have exactly 4 options." }
        require(correctIndex in options.indices) { "correctIndex must match an option." }
    }
}
