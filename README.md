# Driver Theory Quiz App (Android)

This project functions as an offline-first driving theory study app shell aimed at an Ireland-focused theory-test product.

## Current app state

The app now includes:

- Local onboarding with a username-based profile stored on device.
- A themed home dashboard with readiness, session, bookmark, weak-category, and strongest-category summaries.
- Multiple study flows:
  - Practice
  - Quick Study
  - Mini Mock
  - Exam Style Mock
  - Study by Category
  - Weak Question Review
- A dedicated review flow for:
  - All questions
  - Bookmarked questions
  - Category-specific question review
- Per-question explanations, bookmarks, progress tracking, and persistent local study history.
- Optional image support for questions, including road-sign style prompts.
- A profile reset flow that wipes local progress and returns the user to onboarding with the previous username pre-filled.

## Architecture

- `app/src/main/java/com/example/quizprototype/data/content`
  - Bundled JSON question-pack parsing and validation.
- `app/src/main/java/com/example/quizprototype/data/local`
  - Room database, DAOs, entities, and relations for content, bookmarks, sessions, answers, and profile data.
- `app/src/main/java/com/example/quizprototype/data/repository`
  - Repository layer for content import, question bank access, sessions, bookmarks, progress, analytics logging, and user profile actions.
- `app/src/main/java/com/example/quizprototype/domain/model`
  - App-facing models for questions, sessions, dashboard summaries, progress snapshots, and profile state.
- `app/src/main/java/com/example/quizprototype/ui`
  - Navigation Compose screens and `StateFlow`-driven `ViewModel`s for onboarding, home, study setup, review, sessions, results, bookmarks, progress, and settings.

## Main user flows

### Onboarding

- First launch prompts the user to create a local profile with a username.
- If the profile is reset later, onboarding is shown again and the previous alias is prefilled.

### Home

- The home screen shows:
  - Welcome header
  - Dashboard metrics
  - Resume-session entry point when a session is in progress
  - Study mode shortcuts
  - Study library shortcuts

### Study modes

- `Practice`
  - Untimed session with immediate feedback and explanations.
- `Quick Study`
  - Short mixed revision session.
- `Mini Mock`
  - Short timed mock exam.
- `Exam Style Mock`
  - 30-question, 45-minute timed mock.
- `Study by Category`
  - Choose a category first, then run a focused session.

### Review

- `Review the Questions` opens a read-through style flow where:
  - the correct answer is already visible,
  - the user can move question by question,
  - review notes can be toggled on and off,
  - question images render when available.

### Results

- Session completion opens an end-of-quiz screen with:
  - pass/fail state,
  - percentage score,
  - a fuel-gauge style score graphic,
  - category breakdown,
  - bulk bookmarking for incorrectly answered questions.

## Data and content

- Bundled content currently lives in:
  - [app/src/main/assets/content/question_pack_v1.json](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/assets/content/question_pack_v1.json)
- The app currently ships with sample car-theory content:
  - 1 licence type: `CAR`
  - 5 categories
  - multiple topics per category
  - image-backed sample sign questions

Question images can be supplied by:

- drawable resource name, for example `sign_no_entry`
- asset file path, for example `content/images/example_sign.png`

## Persistence model

The app is intentionally offline-first:

- Room is the local source of truth.
- Study sessions, answer history, bookmarks, and the user profile are stored on device.
- Bundled content is imported from JSON into the local database on startup.

## Important implementation notes

- Navigation uses Navigation Compose.
- Screen state is `StateFlow`-based through feature `ViewModel`s.
- The current build still uses destructive Room fallback migration for schema changes during prototype development.
  - Upgrading the schema may clear local progress.
- The shipped content is still sample data and not an official Irish theory dataset.

## Key files

- App entry:
  - [app/src/main/java/com/example/quizprototype/ui/app/DriverTheoryApp.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/app/DriverTheoryApp.kt)
- Navigation routes:
  - [app/src/main/java/com/example/quizprototype/ui/navigation/AppDestinations.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/navigation/AppDestinations.kt)
- Home screen:
  - [app/src/main/java/com/example/quizprototype/ui/home/HomeScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/home/HomeScreen.kt)
- Session screen:
  - [app/src/main/java/com/example/quizprototype/ui/session/SessionScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/session/SessionScreen.kt)
- Results screen:
  - [app/src/main/java/com/example/quizprototype/ui/results/ResultsScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/results/ResultsScreen.kt)
- Review flow:
  - [app/src/main/java/com/example/quizprototype/ui/review/ReviewPickerScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/review/ReviewPickerScreen.kt)
  - [app/src/main/java/com/example/quizprototype/ui/review/ReviewQuestionsScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/review/ReviewQuestionsScreen.kt)
- Settings and reset flow:
  - [app/src/main/java/com/example/quizprototype/ui/settings/SettingsScreen.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/settings/SettingsScreen.kt)
  - [app/src/main/java/com/example/quizprototype/ui/settings/SettingsViewModel.kt](/Users/paddy/AndroidStudioProjects/QuizPrototype/app/src/main/java/com/example/quizprototype/ui/settings/SettingsViewModel.kt)

## Next obvious product steps

- Replace sample content with reviewed Irish driving-theory content.
- Add proper Room migrations instead of relying on destructive fallback.
- Improve review/results presentation and add more polished visual assets.
- Add broader test coverage for onboarding reset, review mode, and timed mock behaviour.

## Release checklist

See [docs/release-checklist.md](/Users/paddy/AndroidStudioProjects/QuizPrototype/docs/release-checklist.md).
