package com.example.quizprototype.ui.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizprototype.data.AppContainer
import com.example.quizprototype.ui.bookmarks.BookmarksEvent
import com.example.quizprototype.ui.bookmarks.BookmarksScreen
import com.example.quizprototype.ui.bookmarks.BookmarksViewModel
import com.example.quizprototype.ui.home.HomeEvent
import com.example.quizprototype.ui.home.HomeScreen
import com.example.quizprototype.ui.home.HomeViewModel
import com.example.quizprototype.ui.navigation.AppDestinations
import com.example.quizprototype.ui.progress.ProgressScreen
import com.example.quizprototype.ui.progress.ProgressViewModel
import com.example.quizprototype.ui.results.ResultsScreen
import com.example.quizprototype.ui.results.ResultsViewModel
import com.example.quizprototype.ui.session.SessionEvent
import com.example.quizprototype.ui.session.SessionScreen
import com.example.quizprototype.ui.session.SessionViewModel
import com.example.quizprototype.ui.settings.SettingsScreen
import com.example.quizprototype.ui.study.StudyModePickerEvent
import com.example.quizprototype.ui.study.StudyModePickerScreen
import com.example.quizprototype.ui.study.StudyModePickerViewModel

@Composable
fun DriverTheoryApp(appContainer: AppContainer) {
    val appStateViewModel: AppStateViewModel = viewModel(
        factory = AppStateViewModel.provideFactory(appContainer.contentImportRepository)
    )
    val appState by appStateViewModel.uiState.collectAsStateWithLifecycle()

    when {
        appState.isLoading -> {
            FullScreenMessage(
                title = "Preparing study content",
                body = "Importing the bundled question pack and loading your study data."
            ) {
                CircularProgressIndicator()
            }
        }

        appState.errorMessage != null -> {
            FullScreenMessage(
                title = "App setup failed",
                body = appState.errorMessage
            )
        }

        else -> {
            DriverTheoryNavGraph(appContainer = appContainer)
        }
    }
}

@Composable
private fun DriverTheoryNavGraph(appContainer: AppContainer) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME
    ) {
        composable(AppDestinations.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    questionBankRepository = appContainer.questionBankRepository,
                    progressRepository = appContainer.progressRepository,
                    studySessionRepository = appContainer.studySessionRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeEvent.OpenSession -> navController.navigate(AppDestinations.sessionRoute(event.sessionId))
                    }
                }
            }

            HomeScreen(
                uiState = uiState,
                onOpenStudyModes = { navController.navigate(AppDestinations.STUDY_PICKER) },
                onResumeSession = { sessionId -> navController.navigate(AppDestinations.sessionRoute(sessionId)) },
                onOpenBookmarks = { navController.navigate(AppDestinations.BOOKMARKS) },
                onOpenProgress = { navController.navigate(AppDestinations.PROGRESS) },
                onOpenSettings = { navController.navigate(AppDestinations.SETTINGS) },
                onStartWeakQuestions = viewModel::startWeakQuestionsSession,
                onDismissMessage = viewModel::clearMessage
            )
        }

        composable(AppDestinations.STUDY_PICKER) {
            val viewModel: StudyModePickerViewModel = viewModel(
                factory = StudyModePickerViewModel.provideFactory(
                    questionBankRepository = appContainer.questionBankRepository,
                    studySessionRepository = appContainer.studySessionRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is StudyModePickerEvent.OpenSession -> navController.navigate(AppDestinations.sessionRoute(event.sessionId))
                    }
                }
            }

            StudyModePickerScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onToggleCategory = viewModel::toggleCategory,
                onClearSelection = viewModel::clearSelection,
                onStartPractice = viewModel::startPracticeSession,
                onStartQuickStudy = viewModel::startQuickStudySession,
                onStartMockExam = viewModel::startMockExamSession,
                onDismissError = viewModel::clearError
            )
        }

        composable(
            route = AppDestinations.SESSION_ROUTE,
            arguments = listOf(navArgument(AppDestinations.SESSION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong(AppDestinations.SESSION_ID_ARG) ?: return@composable
            val viewModel: SessionViewModel = viewModel(
                factory = SessionViewModel.provideFactory(
                    sessionId = sessionId,
                    studySessionRepository = appContainer.studySessionRepository,
                    bookmarkRepository = appContainer.bookmarkRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SessionEvent.OpenResults -> {
                            navController.navigate(AppDestinations.resultsRoute(event.sessionId)) {
                                popUpTo(AppDestinations.SESSION_ROUTE) { inclusive = true }
                            }
                        }
                    }
                }
            }

            SessionScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onSelectOption = viewModel::selectOption,
                onToggleBookmark = viewModel::toggleBookmark,
                onNextQuestion = viewModel::nextQuestion,
                onJumpToQuestion = viewModel::jumpToQuestion,
                onFinishSession = viewModel::finishSession
            )
        }

        composable(
            route = AppDestinations.RESULTS_ROUTE,
            arguments = listOf(navArgument(AppDestinations.SESSION_ID_ARG) { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong(AppDestinations.SESSION_ID_ARG) ?: return@composable
            val viewModel: ResultsViewModel = viewModel(
                factory = ResultsViewModel.provideFactory(
                    sessionId = sessionId,
                    studySessionRepository = appContainer.studySessionRepository,
                    questionBankRepository = appContainer.questionBankRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ResultsScreen(
                uiState = uiState,
                onBackHome = {
                    navController.navigate(AppDestinations.HOME) {
                        popUpTo(AppDestinations.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.PROGRESS) {
            val viewModel: ProgressViewModel = viewModel(
                factory = ProgressViewModel.provideFactory(appContainer.progressRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ProgressScreen(
                snapshot = uiState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.BOOKMARKS) {
            val viewModel: BookmarksViewModel = viewModel(
                factory = BookmarksViewModel.provideFactory(
                    bookmarkRepository = appContainer.bookmarkRepository,
                    studySessionRepository = appContainer.studySessionRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is BookmarksEvent.OpenSession -> navController.navigate(AppDestinations.sessionRoute(event.sessionId))
                    }
                }
            }

            BookmarksScreen(
                questions = uiState,
                onBack = { navController.popBackStack() },
                onStartReview = viewModel::startBookmarkedSession
            )
        }

        composable(AppDestinations.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun FullScreenMessage(
    title: String,
    body: String?,
    content: @Composable (() -> Unit)? = null
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            if (body != null) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            content?.invoke()
        }
    }
}
