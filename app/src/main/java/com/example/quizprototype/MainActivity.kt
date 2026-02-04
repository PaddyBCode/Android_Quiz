package com.example.quizprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizprototype.data.repository.QuizRepository
import com.example.quizprototype.ui.quiz.QuizScreen
import com.example.quizprototype.ui.quiz.QuizViewModel
import com.example.quizprototype.ui.theme.QuizPrototypeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContainer = (application as QuizApplication).appContainer
            QuizPrototypeTheme {
                QuizRoute(repository = appContainer.quizRepository)
            }
        }
    }
}

@Composable
private fun QuizRoute(repository: QuizRepository) {
    val quizViewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.provideFactory(repository)
    )
    QuizScreen(
        uiState = quizViewModel.uiState,
        onOptionSelected = quizViewModel::onOptionSelected,
        onNextQuestion = quizViewModel::onNextQuestion,
        onRestartQuiz = quizViewModel::onRestartQuiz
    )
}
