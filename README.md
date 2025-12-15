# Sustainable Nutrition Tracker

An Android application built with **Kotlin**, **Jetpack Compose**, **Room**, and **MVVM** that helps users track meals, nutritional values, and dietary preferences on a daily basis.

This project is developed **issue-driven** and **user-story oriented**. Every feature is implemented and validated through GitHub Issues.

---

## Project Goals

- Track daily meals with nutritional values
- Support dietary preferences (vegan, vegetarian, meat)
- Provide filtering, sorting, and search
- Offer a clean and reactive UI using Jetpack Compose
- Maintain a clear separation of concerns (UI, ViewModel, Repository, Database)

---

## Development Workflow

### Issues

All development tasks are tracked using **GitHub Issues**.

Each issue represents:
- A **User Story**, or
- A **Technical Task** derived from a User Story

An issue is only closed when it meets the **Definition of Done**.

---

## User Stories

### Example User Stories

- As a user, I want to add a meal with calories and macros so that I can track my nutrition.
- As a user, I want to edit an existing meal so that I can correct mistakes.
- As a user, I want to delete a meal so that my daily list stays accurate.
- As a user, I want to filter meals (vegan, vegetarian, meat) so that I can quickly find relevant meals.
- As a user, I want to see my daily nutrition totals so that I understand my intake for the day.

Each user story is implemented as one or more GitHub Issues.

---

## Issue Structure

Every issue should include:

- Description
- User Story (if applicable)
- Acceptance Criteria
- Definition of Done

### Example Issue

**Title:** Add save button to Add Meal screen

**User Story:**  
As a user, I want a clearly visible save button so that I can save a meal without scrolling.

**Acceptance Criteria:**
- Save button is always visible
- Button triggers meal creation
- App navigates back after saving

---

## Definition of Done

An issue is considered **Done** when:

- The feature works as described in the issue
- The app does not crash
- UI elements are visible and usable
- State is handled via ViewModel / StateFlow
- No unresolved references or compile errors exist
- Manual testing on emulator was successful
- Existing functionality is not broken

---

## Architecture

The project follows **MVVM**:

- **UI (Compose Screens)**  
  AddMealScreen, MealListScreen, EditMealScreen

- **ViewModel**  
  Handles UI state, filtering, sorting, and business logic

- **Repository**  
  Single source of truth for data operations

- **Room Database**  
  Persistent storage for meals

---

## Navigation

Navigation is handled centrally via `AppNavigation`.

Supported flows:
- Meal List → Add Meal
- Meal List → Edit Meal
- Back navigation via top bar or system back button

---

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- Room
- StateFlow / Coroutines
- MVVM Architecture

---

## How to Run

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle
4. Run on an emulator (API 26+ recommended)

---

## Notes

- The project prioritizes working functionality over visual polish
- UI improvements are tracked as separate issues
- Refactors are only allowed if existing behavior remains intact

---

## License

This project is for educational purposes.
