package com.tms.tools.server.grpc

import com.tms.db.TasksTable
import com.tms.db.UsersTable
import com.tms.grpc.CompleteTaskRequest
import com.tms.grpc.GetTaskRequest
import com.tms.grpc.ListUsersRequest
import com.tms.grpc.ListUsersResponse
import com.tms.grpc.ReassignTaskRequest
import com.tms.grpc.Task
import com.tms.grpc.TaskServiceGrpc
import com.tms.grpc.TaskStatus
import com.tms.grpc.User
import com.tms.models.TaskDbRecord
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.testignite.tables.TmsDB.Users

/**
 * gRPC implementation of tms.TaskService, backed by the same Postgres 'tasks'
 * table that the DB showcase asserts against.
 */
class TaskGrpcService : TaskServiceGrpc.TaskServiceImplBase() {

    override fun getTask(request: GetTaskRequest, responseObserver: StreamObserver<Task>) {
        respondWithTask(responseObserver, request.taskId)
    }

    override fun reassignTask(request: ReassignTaskRequest, responseObserver: StreamObserver<Task>) {
        TasksTable.updateTaskAssignee(request.taskId, request.assigneeId)
        respondWithTask(responseObserver, request.taskId)
    }

    override fun completeTask(request: CompleteTaskRequest, responseObserver: StreamObserver<Task>) {
        TasksTable.updateTaskStatus(request.taskId, TaskStatus.DONE.name)
        respondWithTask(responseObserver, request.taskId)
    }

    override fun listUsers(request: ListUsersRequest, responseObserver: StreamObserver<ListUsersResponse>) {
        val response = ListUsersResponse.newBuilder()
            .addAllUsers(UsersTable.selectAllUsers().map { it.toProto() })
            .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    private fun respondWithTask(responseObserver: StreamObserver<Task>, taskId: String) {
        val record = runCatching { TasksTable.selectTask(taskId) }.getOrNull()
        // selectObject returns an empty DTO when no row matches, and the mapper can write
        // SQL NULL into non-null Kotlin fields — treat both as an absent task.
        val recordId: String? = record?.id
        if (record == null || recordId.isNullOrBlank()) {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription("Task '$taskId' not found").asRuntimeException()
            )
            return
        }
        responseObserver.onNext(record.toProto())
        responseObserver.onCompleted()
    }

    private fun Users.toProto(): User =
        User.newBuilder()
            .setId(id ?: "")
            .setName(name ?: "")
            .setTeamId(teamId ?: "")
            .build()

    private fun TaskDbRecord.toProto(): Task {
        // The DTO mapper writes SQL NULL straight into the field, bypassing Kotlin null-safety.
        val nullableAssignee: String? = assigneeId
        return Task.newBuilder()
            .setId(id)
            .setTitle(title)
            .setStatus(TaskStatus.valueOf(status))
            .setAssigneeId(nullableAssignee ?: "")
            .build()
    }
}
