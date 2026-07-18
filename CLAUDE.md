# CLAUDE.md — house rules for tms-demo-autotests

Kotlin + Playwright + Allure UI-automation showcase testing a self-contained mock "Task Management
System" web app. This file is the contract; agents in `.claude/agents/` defer to it.

## Stack

- Kotlin 2.1.20, JVM toolchain 23, Gradle wrapper 8.14 (Kotlin DSL).
- Playwright 1.47.0 (Chromium only, via `BrowserFactory`), JUnit Jupiter 5.11.3, Allure 2.29.0,
  TestIgnite 0.3.1 (main + test — page objects use its `allureStep` helper, tests use its DB tooling).
- Never add `allure-kotlin-*` — it ships no JUnit5 integration and conflicts with `allure-junit5`
  (two competing AllureLifecycle instances; documented in `build.gradle.kts`).
- gRPC: grpc-java 1.82.2 (the version TestIgnite ships) with protobuf 3.25.x codegen via the
  `com.google.protobuf` Gradle plugin. Keep protoc on the 3.25.x line — 4.x gencode does not
  compile against the 3.25 runtime that grpc-protobuf 1.82.2 pulls in.
- REST: rest-assured 6.0.1 at compile scope (the version TestIgnite ships at runtime).

## Layout

- `src/main/kotlin/com/tms/pages/` — Page Object Model (`BasePage`, `pages/components/` with
  `BaseComponent`); `junit/TmsUiExtension.kt` — per-test `Page` injection; `config/TestConfig.kt`;
  `driver/BrowserFactory.kt`; `tools/log/` — Allure step logging listeners (the `allureStep()`
  wrapper itself comes from TestIgnite); `tools/server/` — static server + `GrpcApiServer`.
- `src/main/proto/` — the `tms.TaskService` contract; generated Java lands under
  `build/generated/source/proto/` (never edit or commit it). `grpc/TaskGrpcService.kt` implements
  it against the Postgres `tasks` table; `api/TaskServiceApi.kt` is the test-facing client built on
  TestIgnite's `GrpcClient` (AllureGrpc attachments come for free). `api/TaskRestApi.kt` is the
  REST counterpart, built on RestAssured with the `AllureRestAssured` filter.
- `src/main/resources/webapp/` — the mock app (vanilla HTML/CSS/JS, in-memory state), served on a
  random port per run. Browsers can't speak native gRPC, so its `fetch()` POSTs land on
  `StaticSiteServer`, which forwards them to the gRPC `TaskService` (UI → HTTP gateway → gRPC → DB).
  The gateway doubles as a small REST API: `GET /api/tasks/{id}` answers with the task as proto-JSON.
- `src/test/kotlin/com/tms/tests/` — one scenario per file, class named `<DoingXyz>Test`, split by
  level: `e2e/` (Playwright UI flows), `restapi/` (RestAssured against the REST gateway),
  `grpcapi/` (GrpcClient against the gRPC TaskService).

## Commands

- Run everything: `./gradlew test` (Windows: `.\gradlew.bat test`).
- One test: `./gradlew test --tests "com.tms.tests.e2e.MarkingTaskCompleteTest"`.
- One level: `./gradlew test --tests "com.tms.tests.rest_api.*"` (same for `e2e` / `grpcapi`).
- Debug visually: `-Dheaded=true -DslowMo=200` (never commit config that depends on these).
- Allure report: `./gradlew allureReport` → `build/reports/allure-report/allureReport`.
- Serve the mock app in a real browser: `./gradlew runApp`.

## Style — the non-negotiables

- Page/component public methods: wrapped in `allureStep("Readable action") { ... }`, return `this`
  (or the next page/component). Inside the lambda use `this@ClassName`.
- Assertions: `shouldXyz(...)` methods using `PlaywrightAssertions.assertThat(locator)`. No bare
  JUnit asserts on scraped text, no assertions inside test bodies — tests are a single fluent chain.
- Locators: `getByRole`/`getByLabel` with accessible names first (regex `Pattern` when names vary);
  CSS only for passive elements; **never XPath, never `Thread.sleep`**, rely on auto-wait.
- Tests: `@ExtendWith(TmsUiExtension::class)`, `Page` as method parameter, full Allure set —
  `@Epic("Task Management")`, `@Feature`, `@Story`, `@Severity`, `@DisplayName`, `@Description`.
- Commits: conventional style (`feat:`, `fix:`, `chore:`), imperative mood. Branch, then PR to
  `main` — no direct pushes to `main`.

## Don'ts

- Don't weaken/delete an assertion or raise a timeout to make a test pass — classify the failure
  (app bug vs test bug) and fix the real cause.
- Don't add dependencies without a stated reason.
- Don't commit `.mcp.json` (gitignored — contains the GitHub PAT), `allure-results/`, or anything
  under `build/`.
- Don't leave debug leftovers (unused locals, `VaultData` experiments, commented-out code) in diffs.
