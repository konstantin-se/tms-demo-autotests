package com.tms.tests.e2e

import com.microsoft.playwright.Page
import com.tms.api.TaskServiceApi
import com.tms.grpc.TaskStatus
import com.tms.pages.TaskBoardPage
import com.tms.tools.TmsUiExtension
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
import io.testignite.steps.allureStep
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TmsUiExtension::class)
@Epic("Task Management")
@Feature("Task board")
class MarkingTaskCompletePersistsViaGrpcTest : BaseTest() {

    @Test
    @Story("Task status")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Marking a task complete persists the status to the gRPC TaskService")
    @Description(
        """
        Clicking 'Mark complete' updates the status badge and reaches the backend's gRPC TaskService
        (UI → HTTP gateway → CompleteTask), verified by calling GetTask through TestIgnite's GrpcClient.
        """
    )
    fun markingATaskComplete_persistsViaGrpc(page: Page) {
        allureStep("Given: the gRPC TaskService reports task 't-1' as OPEN") {
            with(TaskServiceApi.getTask("t-1")) {
                check(status == TaskStatus.OPEN) { "Expected precondition status OPEN but was '$status'" }
            }
        }

        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Investigate flaky login")
            .shouldHaveStatus("Open")
            .markComplete()
            .shouldHaveStatus("Done")

        allureStep("Verify the completion reached the backend via gRPC") {
            TaskServiceApi.waitForTaskStatus("t-1", TaskStatus.DONE)

            with(TaskServiceApi.getTask("t-1")) {
                check(status == TaskStatus.DONE) { "Expected gRPC-reported status DONE but was '$status'" }
            }
        }
    }
}
