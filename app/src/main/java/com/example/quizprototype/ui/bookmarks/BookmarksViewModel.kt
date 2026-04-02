package com.example.quizprototype.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizprototype.data.repository.BookmarkRepository
import com.example.quizprototype.data.repository.StudySessionRepository
import com.example.quizprototype.domain.model.BookmarkedQuestion
import com.example.quizprototype.domain.model.QuestionQuery
import com.example.quizprototype.domain.model.SessionConfig
import com.example.quizprototype.domain.model.StudyMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface BookmarksEvent {
    data class OpenSession(val sessionId: Long) : BookmarksEvent
}

class BookmarksViewModel(
    bookmarkRepository: BookmarkRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<BookmarksEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<List<BookmarkedQuestion>> = bookmarkRepository.observeBookmarkedQuestions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun startBookmarkedSession() {
        viewModelScope.launch {
            runCatching {
                studySessionRepository.startSession(
                    SessionConfig(
                        mode = StudyMode.BOOKMARKED,
                        title = "Bookmarked Questions",
                        query = QuestionQuery(bookmarkedOnly = true),
                        questionLimit = null,
                        durationLimitSeconds = null,
                        immediateFeedback = true,
                        allowReviewBeforeSubmit = true
                    )
                )
            }.onSuccess { sessionId ->
                _events.emit(BookmarksEvent.OpenSession(sessionId))
            }
        }
    }

    companion object {
        fun provideFactory(
            bookmarkRepository: BookmarkRepository,
            studySessionRepository: StudySessionRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BookmarksViewModel(
                        bookmarkRepository = bookmarkRepository,
                        studySessionRepository = studySessionRepository
                    ) as T
                }
            }
    }
}
