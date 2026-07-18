# TMS Example Autotests

A self-contained showcase of a Kotlin + Playwright + Allure UI automation framework: Page Object
Model, fluent-chain tests, and readable Allure steps — applied to a small mock "Task Management
System" (TMS) instead of any real product.

## What's here

- `src/test/resources/webapp/` — a static, backend-free mock web app (vanilla HTML/CSS/JS, in-memory
  state): pick a team, view/filter/sort its tasks, assign or reassign a task to a user, view task
  details, mark a task complete.
- `src/main/kotlin/com/tms/tools/server/StaticSiteServer.kt` — a zero-dependency JDK `HttpServer`
  that serves the mock app on a random local port for the duration of the test run.
- `src/main/kotlin/com/tms/pages/` — the Page Object Model: `BasePage`/`BaseComponent`, a
  `TaskBoardPage` composed of `TeamSelector`, `TaskFilterBar`, `TaskTable` (which exposes `TaskRow`
  components), and `AssignTaskDialog`/`TaskDetailDialog`.
- `src/main/kotlin/com/tms/junit/TmsUiExtension.kt` — a JUnit5 `ParameterResolver` that injects a
  fresh Playwright `Page` per test (`Browser` reused per JVM, `BrowserContext` fresh per test).
- `src/test/kotlin/com/tms/tests/` — 10 scenario-oriented tests covering the full user flow.

## Running

```
./gradlew test allureReport
```

Report is generated at `build/reports/allure-report/allureReport`.
