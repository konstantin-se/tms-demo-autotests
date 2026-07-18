package com.tms.api

import com.google.protobuf.util.JsonFormat
import com.tms.config.TestConfig
import com.tms.grpc.Task
import com.tms.grpc.TaskStatus
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import io.testignite.steps.allureStep

/**
 * Test-facing RestAssured client for the mock app's REST gateway. GET responses
 * are parsed back into the proto [Task], so REST and gRPC tests assert against
 * the same model.
 */
object TaskRestApi {

    private val requestSpec: RequestSpecification by lazy {
        RequestSpecBuilder()
            .setBaseUri(TestConfig.baseUrl)
            .build()
    }

    fun getTask(taskId: String): Task =
        allureStep("REST: GET /api/tasks/$taskId") {
            val json = given(requestSpec)
                .get("/api/tasks/{taskId}", taskId)
                .then()
                .statusCode(200)
                .extract().body().asString()
            Task.newBuilder().also { JsonFormat.parser().ignoringUnknownFields().merge(json, it) }.build()
        }

    fun completeTask(taskId: String): Unit =
        allureStep("REST: POST /api/tasks/$taskId/status → ${TaskStatus.DONE.name}") {
            given(requestSpec)
                .contentType(ContentType.TEXT)
                .body(TaskStatus.DONE.name)
                .post("/api/tasks/{taskId}/status", taskId)
                .then()
                .statusCode(204)
        }
}
