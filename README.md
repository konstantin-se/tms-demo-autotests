# TMS Example Autotests

A self-contained showcase of a Kotlin + Playwright + Allure test-automation framework built on
[TestIgnite](https://github.com/konstantin-se/TestIgnite), applied to a small mock "Task Management
System" (TMS) instead of any real product. The mock is a full vertical stack that boots inside the
test run:

```
browser UI  →  HTTP gateway (JDK HttpServer)  →  gRPC TaskService  →  Postgres (Testcontainers)
```

Tests attack it at three levels — `e2e/` (Playwright UI flows), `rest_api/` (RestAssured against
the gateway), `grpc_api/` (gRPC calls against the TaskService) — and cross-check results in the
database.

## TestIgnite showcases

### 1. A gRPC API client in a dozen lines

`TestIgnite`'s `GrpcClient` hands you a ready channel with console logging and Allure
request/response attachments (AllureGrpc) already wired in. The whole test-facing client for the
TaskService (`src/main/kotlin/com/tms/api/TaskServiceApi.kt`) is just a stub over that channel plus
readable step wrappers:

```kotlin
private val grpcClient = GrpcClient("localhost", GrpcApiServer.port.toString())

private val taskService: TaskServiceGrpc.TaskServiceBlockingStub
    get() = TaskServiceGrpc.newBlockingStub(grpcClient.channel)

fun reassignTask(taskId: String, assigneeId: String): Task =
    allureStep("gRPC: ReassignTask '$taskId' → '$assigneeId'") {
        taskService.reassignTask(
            ReassignTaskRequest.newBuilder().setTaskId(taskId).setAssigneeId(assigneeId).build()
        )
    }
```

Every RPC a test makes shows up in the Allure report with its request and response payloads —
no interceptors or logging code in this repo.

### 2. DB rows auto-mapped into classes

`DBSqlExecutor.selectObject` / `selectListObjects` map a result set onto a class by matching
column names to fields (`assignee_id` → `assigneeId` — underscores and case are normalized away),
attach the fetched data to Allure, and return typed objects. The SQL layer
(`src/main/kotlin/com/tms/db/`) stays tiny:

```kotlin
fun selectTask(taskId: String): TaskDbRecord =
    tmsDB.selectObject("SELECT id, title, status, assignee_id FROM tasks WHERE id = '$taskId'",
        TaskDbRecord::class.java)

fun selectTeamUsers(teamId: String): List<Users> =
    tmsDB.selectListObjects("SELECT id, name, team_id FROM users WHERE team_id = '$teamId' ORDER BY id",
        Users::class.java)
```

### 3. Data classes generated from DB metadata before compilation

The `Users` class above is not written by hand — it does not exist in `src/` at all. TestIgnite's
`DtoClassGenerator` connects to the database at build time, reads table metadata over JDBC, and
generates Kotlin data classes that main code compiles against:

```yaml
# src/main/resources/dto_generation_config.yml
databases:
  - dbName: TmsDB
    fieldAsCamelCase: true
    tables:
      - users
```

The `generateDtoClasses` Gradle task runs before `compileKotlin`: it boots a throwaway Postgres
with the project's init script, reflects into the connector object the generator resolves by
`dbName` (`src/dtoGen/kotlin/io/testignite/database/connectors/TmsDB.kt` — the same object that
serves as the runtime connector), and writes `io.testignite.tables.TmsDB.Users` into
`build/generated/sources/dto/`. Change the table schema and the class follows on the next build;
nothing generated is ever committed. `UsersTable`, the gRPC service, and the tests all consume the
generated class.

## What's here

- `src/main/resources/webapp/` — the mock app (vanilla HTML/CSS/JS). Its user list is not
  hardcoded: the page fetches `GET /api/users`, which the gateway serves from the `users` table
  via gRPC `ListUsers`.
- `src/main/kotlin/com/tms/tools/server/` — `StaticSiteServer` (static files + HTTP-to-gRPC
  gateway, since browsers can't speak native gRPC) and `GrpcApiServer` hosting the
  `tms.TaskService` implementation backed by Postgres.
- `src/main/proto/tms/task_service.proto` — the gRPC contract; Java stubs are generated at build.
- `src/main/kotlin/com/tms/pages/` — the Page Object Model: fluent chains, `allureStep`-wrapped
  actions, Playwright auto-waiting assertions.
- `src/main/kotlin/com/tms/tools/TmsUiExtension.kt` — JUnit5 extension injecting the Playwright
  `Page`: one shared browser window for the whole run, tests reset state by navigating.
- `src/main/kotlin/com/tms/db/` — the DB layer: `TmsDBConnector` (typealias to the shared
  connector owning the Testcontainers Postgres) and SQL-only objects `TasksTable` / `UsersTable`.
- `src/test/kotlin/com/tms/tests/` — 18 scenario tests split by level: `e2e/`, `rest_api/`,
  `grpc_api/`.

## Running

Requires Docker (Testcontainers boots Postgres both for DTO generation at build time and for the
test run).

```
./gradlew test allureReport     # full suite + report at build/reports/allure-report/allureReport
./gradlew test --tests "com.tms.tests.e2e.*"      # one level only
./gradlew generateDtoClasses    # just regenerate table DTOs
./gradlew runApp                # serve the mock app to click through it yourself
```

Debug visually with `-Dheaded=true -DslowMo=200`.
