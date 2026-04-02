# Driver Theory Quiz (Android)

This project is structured for maintainability and feature growth:

- `data/local`: Room database, entities, DAO, and relationship models.
- `data/repository`: Repository abstraction and implementation.
- `data/seed`: Local sample quiz seed data.
- `domain/model`: App-facing quiz models.
- `ui/quiz`: Quiz UI, state model, and ViewModel.

## Current features

- One local multiple-choice quiz.
- Quick Quiz mode serves 5 random questions from all categories.
- 25 sample questions (5 per category).
- Questions with four options each.
- Fixed question categories:
  - `Control of Vehicle`
  - `Legal Matters/Rules of the Road`
  - `Managing Risk`
  - `Safe and Social Responsible Driving`
  - `Technical Matters`
- Local Room persistence and startup seed pipeline.
- Quiz flow with score calculation and restart.

## Extend later

- Add more quizzes by inserting additional `QuizEntity`, `QuestionEntity`, and `AnswerOptionEntity` rows.
- Add user progress/history tables (attempts, scores, bookmarks).
- Add category filtering and timed tests using new repository APIs and ViewModels.
