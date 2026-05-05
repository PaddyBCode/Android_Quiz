package com.example.quizprototype.ui.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizprototype.data.AppContainer
import com.example.quizprototype.ui.achievements.AchievementsScreen
import com.example.quizprototype.ui.achievements.AchievementUnlockToastHost
import com.example.quizprototype.ui.achievements.AchievementsViewModel
import com.example.quizprototype.ui.bookmarks.BookmarksEvent
import com.example.quizprototype.ui.bookmarks.BookmarksScreen
import com.example.quizprototype.ui.bookmarks.BookmarksViewModel
import com.example.quizprototype.ui.home.HomeEvent
import com.example.quizprototype.ui.home.HomeScreen
import com.example.quizprototype.ui.home.HomeViewModel
import com.example.quizprototype.ui.navigation.AppDestinations
import com.example.quizprototype.ui.onboarding.OnboardingEvent
import com.example.quizprototype.ui.onboarding.OnboardingScreen
import com.example.quizprototype.ui.onboarding.OnboardingViewModel
import com.example.quizprototype.ui.progress.ProgressScreen
import com.example.quizprototype.ui.progress.ProgressViewModel
import com.example.quizprototype.ui.review.ReviewPickerScreen
import com.example.quizprototype.ui.review.ReviewPickerViewModel
import com.example.quizprototype.ui.review.ReviewQuestionsScreen
import com.example.quizprototype.ui.review.ReviewQuestionsViewModel
import com.example.quizprototype.ui.review.ReviewScope
import com.example.quizprototype.ui.results.ResultsScreen
import com.example.quizprototype.ui.results.ResultsViewModel
import com.example.quizprototype.ui.session.SessionEvent
import com.example.quizprototype.ui.session.SessionScreen
import com.example.quizprototype.ui.session.SessionViewModel
import com.example.quizprototype.ui.settings.SettingsEvent
import com.example.quizprototype.ui.settings.SettingsScreen
import com.example.quizprototype.ui.settings.SettingsViewModel
import com.example.quizprototype.ui.study_room.CategoryStudyEvent
import com.example.quizprototype.ui.study_room.CategoryStudyScreen
import com.example.quizprototype.ui.study_room.CategoryStudyViewModel
import com.example.quizprototype.ui.study_room.StudyModePickerEvent
import com.example.quizprototype.ui.study_room.StudyModePickerScreen
import com.example.quizprototype.ui.study_room.StudyModePickerViewModel
import com.example.quizprototype.ui.theme.QuizPrototypeTheme
import com.example.quizprototype.ui.user.UserScreen
import com.example.quizprototype.ui.user.UserViewModel
import com.example.quizprototype.domain.model.AppThemeMode

@Composable
fun DriverTheoryApp(appContainer: AppContainer) {
    val appStateViewModel: AppStateViewModel = viewModel(
        factory = AppStateViewModel.provideFactory(
            contentImportRepository = appContainer.contentImportRepository,
            userProfileRepository = appContainer.userProfileRepository
        )
    )
    val appState by appStateViewModel.uiState.collectAsStateWithLifecycle()

    QuizPrototypeTheme(
        darkTheme = appState.themeMode == AppThemeMode.DARK
    ) {
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
                Box(modifier = Modifier.fillMaxSize()) {
                    DriverTheoryNavGraph(
                        appContainer = appContainer,
                        hasUserProfile = appState.userProfile != null
                    )
                    AchievementUnlockToastHost(
                        achievementsRepository = appContainer.achievementsRepository,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun DriverTheoryNavGraph(
    appContainer: AppContainer,
    hasUserProfile: Boolean
) {
    val navController = rememberNavController()
    val bottomNavItems = remember {
        listOf(
            BottomNavItem(AppDestinations.HOME, BottomNavIcon.HOME, "Home"),
            BottomNavItem(AppDestinations.REVIEW_PICKER, BottomNavIcon.STUDY, "Study room"),
            BottomNavItem(AppDestinations.ACHIEVEMENTS, BottomNavIcon.ACHIEVEMENTS, "Achievements"),
            BottomNavItem(AppDestinations.USER, BottomNavIcon.USER, "User")
        )
    }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                QuizBottomNavigationBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onDestinationSelected = navController::navigateToBottomDestination
                )
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (hasUserProfile) AppDestinations.HOME else AppDestinations.onboardingRoute()
        ) {
        composable(
            route = AppDestinations.ONBOARDING_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.PREFILL_USERNAME_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val prefillUsername = backStackEntry.arguments?.getString(AppDestinations.PREFILL_USERNAME_ARG).orEmpty()
            val viewModel: OnboardingViewModel = viewModel(
                key = "onboarding:$prefillUsername",
                factory = OnboardingViewModel.provideFactory(
                    initialUsername = prefillUsername,
                    userProfileRepository = appContainer.userProfileRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        OnboardingEvent.ProfileCreated -> {
                            navController.navigate(AppDestinations.HOME) {
                                popUpTo(AppDestinations.ONBOARDING_ROUTE) { inclusive = true }
                            }
                        }
                    }
                }
            }

            OnboardingScreen(
                uiState = uiState,
                onAvatarSelected = viewModel::onAvatarSelected,
                onUsernameChanged = viewModel::onUsernameChanged,
                onCreateProfile = viewModel::createProfile
            )
        }

        composable(AppDestinations.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    questionBankRepository = appContainer.questionBankRepository,
                    progressRepository = appContainer.progressRepository,
                    userProfileRepository = appContainer.userProfileRepository,
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
                onResumeSession = { sessionId -> navController.navigate(AppDestinations.sessionRoute(sessionId)) },
                onOpenCategoryStudy = { navController.navigate(AppDestinations.CATEGORY_STUDY) },
                onOpenAchievements = { navController.navigate(AppDestinations.ACHIEVEMENTS) },
                onOpenReviewQuestions = { navController.navigate(AppDestinations.REVIEW_PICKER) },
                onOpenBookmarks = { navController.navigate(AppDestinations.BOOKMARKS) },
                onOpenSettings = { navController.navigate(AppDestinations.SETTINGS) },
                onStartQuickStudy = viewModel::startQuickStudySession,
                onStartMiniMock = viewModel::startMiniMockSession,
                onStartExamStyleMock = viewModel::startExamStyleMockSession,
                onStartWeakQuestions = viewModel::startWeakQuestionsSession,
                onDismissMessage = viewModel::clearMessage
            )
        }

        composable(AppDestinations.STUDY_PICKER) {
            val viewModel: StudyModePickerViewModel = viewModel(
                factory = StudyModePickerViewModel.provideFactory(
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
                onOpenStudyByCategory = { navController.navigate(AppDestinations.CATEGORY_STUDY) },
                onOpenReviewQuestions = { navController.navigate(AppDestinations.REVIEW_PICKER) },
                onStartPractice = viewModel::startPracticeSession,
                onStartQuickStudy = viewModel::startQuickStudySession,
                onStartMiniMock = viewModel::startMiniMockSession,
                onStartExamStyleMock = viewModel::startExamStyleMockSession,
                onDismissError = viewModel::clearError
            )
        }

        composable(AppDestinations.CATEGORY_STUDY) {
            val viewModel: CategoryStudyViewModel = viewModel(
                factory = CategoryStudyViewModel.provideFactory(
                    questionBankRepository = appContainer.questionBankRepository,
                    studySessionRepository = appContainer.studySessionRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is CategoryStudyEvent.OpenSession -> navController.navigate(AppDestinations.sessionRoute(event.sessionId))
                    }
                }
            }

            CategoryStudyScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onStartCategory = viewModel::startCategorySession,
                onDismissError = viewModel::clearError
            )
        }

        composable(AppDestinations.REVIEW_PICKER) {
            val viewModel: ReviewPickerViewModel = viewModel(
                factory = ReviewPickerViewModel.provideFactory(appContainer.questionBankRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ReviewPickerScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onOpenAllQuestions = {
                    navController.navigate(AppDestinations.reviewQuestionsRoute(ReviewScope.ALL.name))
                },
                onOpenBookmarkedQuestions = {
                    navController.navigate(AppDestinations.reviewQuestionsRoute(ReviewScope.BOOKMARKED.name))
                },
                onOpenCategory = { category ->
                    navController.navigate(
                        AppDestinations.reviewQuestionsRoute(
                            scope = ReviewScope.CATEGORY.name,
                            filterId = category.id
                        )
                    )
                }
            )
        }

        composable(AppDestinations.ACHIEVEMENTS) {
            val viewModel: AchievementsViewModel = viewModel(
                factory = AchievementsViewModel.provideFactory(
                    achievementsRepository = appContainer.achievementsRepository,
                    userProfileRepository = appContainer.userProfileRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AchievementsScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.USER) {
            val viewModel: UserViewModel = viewModel(
                factory = UserViewModel.provideFactory(
                    userProfileRepository = appContainer.userProfileRepository,
                    progressRepository = appContainer.progressRepository,
                    achievementsRepository = appContainer.achievementsRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            UserScreen(uiState = uiState)
        }

        composable(
            route = AppDestinations.REVIEW_QUESTIONS_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.REVIEW_SCOPE_ARG) { type = NavType.StringType },
                navArgument(AppDestinations.REVIEW_FILTER_ID_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scopeValue = backStackEntry.arguments?.getString(AppDestinations.REVIEW_SCOPE_ARG) ?: ReviewScope.ALL.name
            val filterId = backStackEntry.arguments?.getString(AppDestinations.REVIEW_FILTER_ID_ARG) ?: "none"
            val reviewScope = ReviewScope.fromRoute(scopeValue)
            val viewModel: ReviewQuestionsViewModel = viewModel(
                factory = ReviewQuestionsViewModel.provideFactory(
                    scope = reviewScope,
                    filterId = filterId,
                    questionBankRepository = appContainer.questionBankRepository,
                    achievementsRepository = appContainer.achievementsRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ReviewQuestionsScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onJumpToQuestion = viewModel::jumpToQuestion,
                onPreviousQuestion = viewModel::previousQuestion,
                onNextQuestion = viewModel::nextQuestion,
                onToggleNotes = viewModel::toggleNotes
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
                    questionBankRepository = appContainer.questionBankRepository,
                    bookmarkRepository = appContainer.bookmarkRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ResultsScreen(
                uiState = uiState,
                onBookmarkIncorrect = viewModel::bookmarkIncorrectQuestions,
                onDismissMessage = viewModel::clearMessage,
                onBackHome = {
                    navController.navigate(AppDestinations.HOME) {
                        popUpTo(AppDestinations.HOME) { inclusive = false }
                        launchSingleTop = true
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
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(appContainer.userProfileRepository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SettingsEvent.ProfileReset -> {
                            navController.navigate(
                                AppDestinations.onboardingRoute(event.previousUsername)
                            ) {
                                popUpTo(AppDestinations.HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }

            SettingsScreen(
                uiState = uiState,
                onThemeModeSelected = viewModel::updateThemeMode,
                onProfileAvatarSelected = viewModel::updateProfileAvatar,
                onConfirmReset = viewModel::resetProfile,
                onDismissError = viewModel::clearError,
                onBack = { navController.popBackStack() }
            )
        }
    }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: BottomNavIcon,
    val contentDescription: String
)

private enum class BottomNavIcon {
    HOME,
    STUDY,
    ACHIEVEMENTS,
    USER
}

private fun NavHostController.navigateToBottomDestination(route: String) {
    navigate(route) {
        popUpTo(AppDestinations.HOME) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun QuizBottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onDestinationSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(58.dp)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                IconButton(onClick = { onDestinationSelected(item.route) }) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BottomNavIcon(
                            icon = item.icon,
                            selected = selected,
                            contentDescription = item.contentDescription
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    icon: BottomNavIcon,
    selected: Boolean,
    contentDescription: String
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
    }
    Canvas(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .semantics { this.contentDescription = contentDescription }
    ) {
        val stroke = Stroke(
            width = 2.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        when (icon) {
            BottomNavIcon.HOME -> {
                val roof = Path().apply {
                    moveTo(size.width * 0.18f, size.height * 0.48f)
                    lineTo(size.width * 0.5f, size.height * 0.2f)
                    lineTo(size.width * 0.82f, size.height * 0.48f)
                }
                val body = Path().apply {
                    moveTo(size.width * 0.28f, size.height * 0.45f)
                    lineTo(size.width * 0.28f, size.height * 0.78f)
                    lineTo(size.width * 0.72f, size.height * 0.78f)
                    lineTo(size.width * 0.72f, size.height * 0.45f)
                }
                drawPath(roof, color = color, style = stroke)
                drawPath(body, color = color, style = stroke)
            }

            BottomNavIcon.STUDY -> {
                val barWidth = size.width * 0.14f
                listOf(0.3f to 0.34f, 0.5f to 0.52f, 0.7f to 0.72f).forEach { (centerX, heightRatio) ->
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(
                            x = size.width * centerX - barWidth / 2f,
                            y = size.height * (0.82f - heightRatio)
                        ),
                        size = Size(barWidth, size.height * heightRatio),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                        style = stroke
                    )
                }
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.18f, size.height * 0.82f),
                    end = Offset(size.width * 0.82f, size.height * 0.82f),
                    strokeWidth = stroke.width,
                    cap = StrokeCap.Round
                )
            }

            BottomNavIcon.ACHIEVEMENTS -> {
                val star = Path()
                val points = 10
                val outerRadius = size.minDimension * 0.36f
                val innerRadius = outerRadius * 0.46f
                val center = Offset(size.width / 2f, size.height / 2f)
                repeat(points) { index ->
                    val radius = if (index % 2 == 0) outerRadius else innerRadius
                    val angle = (-90f + index * 36f) * Math.PI.toFloat() / 180f
                    val point = Offset(
                        center.x + kotlin.math.cos(angle) * radius,
                        center.y + kotlin.math.sin(angle) * radius
                    )
                    if (index == 0) star.moveTo(point.x, point.y) else star.lineTo(point.x, point.y)
                }
                star.close()
                drawPath(star, color = color, style = stroke)
            }

            BottomNavIcon.USER -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.16f,
                    center = Offset(size.width * 0.5f, size.height * 0.32f),
                    style = stroke
                )
                drawArc(
                    color = color,
                    startAngle = 28f,
                    sweepAngle = 124f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.22f, size.height * 0.45f),
                    size = Size(size.width * 0.56f, size.height * 0.44f),
                    style = stroke
                )
            }
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
