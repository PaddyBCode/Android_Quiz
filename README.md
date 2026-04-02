# Driver Theory Quiz (Android)

This project has been expanded from a two-screen prototype into an offline-first study app shell for a production-style driving theory product.

## Current architecture

- `data/content`: Bundled JSON question pack parsing and import validation.
- `data/local`: Room database, DAOs, entities, and relations for content, bookmarks, sessions, and answer history.
- `data/repository`: Feature-focused repositories for content import, question bank access, bookmarks, sessions, and progress.
- `domain/model`: App-facing study, progress, and question models.
- `ui/*`: Navigation Compose screens and StateFlow-driven ViewModels for home, study setup, sessions, results, bookmarks, progress, and settings.

## Implemented features

- Bundled JSON content import into Room on first launch.
- Home dashboard with readiness summary, resume-session entry point, and weakest-category hints.
- Category-filtered practice mode with immediate explanations.
- Quick study mode for short mixed sessions.
- Timed mock exam mode with delayed review until completion.
- Persisted study sessions and answer history.
- Results review with per-question explanations and category breakdown.
- Bookmarking and bookmarked-only review sessions.
- Progress tracking using completed session history.
- Weak-question review generated from prior incorrect answers.

## Next content step

- Replace the bundled sample JSON file in `app/src/main/assets/content/question_pack_v1.json` with validated Irish driving theory content.
- Follow the release checklist in [docs/release-checklist.md](/Users/paddy/AndroidStudioProjects/QuizPrototype/docs/release-checklist.md).
