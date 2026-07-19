package com.tms.tests.e2e

import com.microsoft.playwright.Page
import com.tms.db.TasksTable
import com.tms.pages.TaskBoardPage
import com.tms.tools.TmsUiExtension
import com.tms.tools.server.StaticSiteServer
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
import io.testignite.steps.allureStep
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TmsUiExtension::class)
@Epic("Task Management")
@Feature("Task assignment")
class ReassigningTaskPersistsToDatabaseTest : BaseTest() {

    @Test
    @Story("Reassign task")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Reassigning a task persists the new assignee to the database")
    @Description(
        """
        Reassigning a task through the UI updates the assignee on screen and in the backing
        Postgres 'tasks' table, verified via TestIgnite's DBSqlExecutor.
        """
    )
    fun reassigningTask_persistsToDatabase(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Investigate flaky login")
            .shouldHaveAssignee("Unassigned")
            .openAssignDialog()
            .selectAssignee("Marcus Lee")
            .confirm()
            .backToTask()
            .shouldHaveAssignee("Marcus Lee")

        allureStep("Verify the reassignment persisted to the database") {
            TasksTable.waitForTaskAssignee("t-1", "u-marcus")

            with(TasksTable.selectTask("t-1")) {
                check(assigneeId == "u-marcus") { "Expected DTO-mapped assignee_id 'u-marcus' but was '${assigneeId}'" }
            }
        }
    }
}
