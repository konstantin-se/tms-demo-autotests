package com.tms.tests.grpc_api

import com.tms.api.TaskServiceApi
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
class ReassigningTaskViaGrpcApiTest : BaseTest() {

    private val taskId = "t-grpc-1"

    @Test
    @Story("Reassign task")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Reassigning a task through the gRPC TaskService persists the new assignee")
    @Description(
        """Calling ReassignTask on the gRPC TaskService through TestIgnite's GrpcClient moves the task
            to the new assignee, verified from the RPC response, a follow-up GetTask, and the backing
            Postgres 'tasks' row."""
    )
    fun reassigningATaskViaGrpc_persistsTheAssignee() {
        allureStep("Given: task '$taskId' exists and gRPC reports it unassigned") {
            TasksTable.insertTask(taskId, "Verify gRPC TaskService", TaskStatus.OPEN.name)
            with(TaskServiceApi.getTask(taskId)) {
                check(assigneeId.isEmpty()) { "Expected precondition unassigned but assignee was '$assigneeId'" }
            }
        }

        allureStep("When: ReassignTask moves the task to 'u-marcus'") {
            with(TaskServiceApi.reassignTask(taskId, "u-marcus")) {
                check(assigneeId == "u-marcus") { "Expected RPC response assignee 'u-marcus' but was '$assigneeId'" }
            }
        }

        allureStep("Then: GetTask and the database both report assignee 'u-marcus'") {
            with(TaskServiceApi.getTask(taskId)) {
                check(assigneeId == "u-marcus") { "Expected gRPC-reported assignee 'u-marcus' but was '$assigneeId'" }
            }
            with(TasksTable.selectTask(taskId)) {
                check(assigneeId == "u-marcus") { "Expected DB assignee 'u-marcus' but was '$assigneeId'" }
            }
        }
    }

    @AfterEach
    fun cleanUpTask() {
        TasksTable.deleteTask(taskId)
    }
}
