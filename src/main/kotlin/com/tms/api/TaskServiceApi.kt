package com.tms.api

import com.tms.grpc.GetTaskRequest
import com.tms.grpc.ReassignTaskRequest
import com.tms.grpc.Task
import com.tms.grpc.TaskServiceGrpc
import com.tms.grpc.TaskStatus
import com.tms.tools.server.GrpcApiServer
import io.testignite.grpc.GrpcClient
import io.testignite.steps.allureStep
import io.testignite.utils.wait.waitForWithAssert
import java.util.concurrent.TimeUnit

/**
 * Test-facing client for the mock backend's TaskService, wired through
 * TestIgnite's GrpcClient (its channel logs calls and attaches request/response
 * payloads to Allure via the AllureGrpc interceptor).
 */
object TaskServiceApi {

    private val grpcClient by lazy { GrpcClient("localhost", GrpcApiServer.port.toString()) }

    private val taskService: TaskServiceGrpc.TaskServiceBlockingStub
        get() = TaskServiceGrpc.newBlockingStub(grpcClient.channel)

    fun getTask(taskId: String): Task =
        allureStep("gRPC: GetTask '$taskId'") {
            taskService.getTask(GetTaskRequest.newBuilder().setTaskId(taskId).build())
        }

    fun reassignTask(taskId: String, assigneeId: String): Task =
        allureStep("gRPC: ReassignTask '$taskId' → '$assigneeId'") {
            taskService.reassignTask(
                ReassignTaskRequest.newBuilder().setTaskId(taskId).setAssigneeId(assigneeId).build()
            )
        }

    fun waitForTaskStatus(taskId: String, status: TaskStatus): Unit =
        allureStep("gRPC: wait until task '$taskId' has status ${status.name}") {
            waitForWithAssert(5, 1, TimeUnit.SECONDS) {
                val actual = getTask(taskId).status
                check(actual == status) { "Expected task '$taskId' to have status $status but was $actual" }
            }
        }
}
