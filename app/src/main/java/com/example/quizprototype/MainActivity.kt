package com.example.quizprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizprototype.data.repository.QuizRepository
import com.example.quizprototype.ui.menu.MainMenuScreen
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
                QuizPrototypeApp(repository = appContainer.quizRepository)
            }
        }
    }
}

@Composable
private fun QuizPrototypeApp(repository: QuizRepository) {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.MAIN_MENU) }

    when (currentScreen) {
        AppScreen.MAIN_MENU -> {
            MainMenuScreen(
                onQuickQuizClick = { currentScreen = AppScreen.QUIZ },
                onCategoryQuizClick = {},
                onProfileClick = {}
            )
        }

        AppScreen.QUIZ -> {
            QuizRoute(
                repository = repository,
                onBackToMenu = { currentScreen = AppScreen.MAIN_MENU }
            )
        }
    }
}

@Composable
private fun QuizRoute(
    repository: QuizRepository,
    onBackToMenu: () -> Unit
) {
    val quizViewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.provideFactory(repository)
    )
    QuizScreen(
        uiState = quizViewModel.uiState,
        onOptionSelected = quizViewModel::onOptionSelected,
        onNextQuestion = quizViewModel::onNextQuestion,
        onRestartQuiz = quizViewModel::onRestartQuiz,
        onBackToMenu = onBackToMenu
    )
}

private enum class AppScreen {
    MAIN_MENU,
    QUIZ
}
