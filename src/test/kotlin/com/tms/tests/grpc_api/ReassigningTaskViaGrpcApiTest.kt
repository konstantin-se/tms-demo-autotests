package com.tms.tests.grpc_api

import com.tms.api.TaskServiceApi
import com.tms.tools.shouldBe
import com.tms.db.TasksTable
import com.tms.grpc.TaskStatus
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
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
        """
        Calling ReassignTask on the gRPC TaskService through TestIgnite's GrpcClient moves the task
        to the new assignee, verified from the RPC response, a follow-up GetTask, and the backing
        Postgres 'tasks' row.
        """
    )
    fun reassigningATaskViaGrpc_persistsTheAssignee() {
        TasksTable.insertTask(taskId, "Verify gRPC TaskService", TaskStatus.OPEN.name)
        TaskServiceApi.getTask(taskId).assigneeId.shouldBe("", "precondition assignee")

        TaskServiceApi.reassignTask(taskId, "u-marcus").assigneeId.shouldBe("u-marcus", "RPC response assignee")

        TaskServiceApi.getTask(taskId).assigneeId.shouldBe("u-marcus")
        TasksTable.selectTask(taskId).assigneeId.shouldBe("u-marcus")
    }

    @AfterEach
    fun cleanUpTask() {
        TasksTable.deleteTask(taskId)
    }
}
