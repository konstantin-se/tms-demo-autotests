package com.tms.tests.restapi

import com.tms.api.TaskRestApi
import com.tms.db.TasksTable
import com.tms.grpc.TaskStatus
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
import io.testignite.steps.allureStep
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Epic("Task Management")
@Feature("Task API")
class CompletingTaskViaRestApiTest : BaseTest() {

    private val taskId = "t-rest-1"

    @Test
    @Story("Task status")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Completing a task through the REST gateway persists the DONE status")
    @Description(
        """POSTing DONE to /api/tasks/{id}/status flows through the HTTP gateway to the gRPC TaskService
and lands in the Postgres 'tasks' table, verified by reading the task back over REST
(GET /api/tasks/{id}) and straight from the database."""
    )
    fun completingATaskViaRest_persistsTheStatus() {
        allureStep("Given: task '$taskId' exists and REST reports it as OPEN") {
            TasksTable.insertTask(taskId, "Verify REST gateway", TaskStatus.OPEN.name)
            with(TaskRestApi.getTask(taskId)) {
                check(status == TaskStatus.OPEN) { "Expected precondition status OPEN but was '$status'" }
            }
        }

        TaskRestApi.completeTask(taskId)

        allureStep("Then: REST and the database both report the task as DONE") {
            with(TaskRestApi.getTask(taskId)) {
                check(status == TaskStatus.DONE) { "Expected REST-reported status DONE but was '$status'" }
            }
            with(TasksTable.selectTask(taskId)) {
                check(status == TaskStatus.DONE.name) { "Expected DB status DONE but was '$status'" }
            }
        }
    }

    @AfterEach
    fun cleanUpTask() {
        TasksTable.deleteTask(taskId)
    }
}
