package com.tms.tests.e2e

import com.microsoft.playwright.Page
import com.tms.db.TasksTable
import com.tms.db.UsersTable
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
@Feature("Task assignment")
class AssigningTaskToUserFromUsersTableTest : BaseTest() {

    @Test
    @Story("Assign task")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Assigning a task to a user read from the 'users' table")
    @Description(
        """
        Reads a user row from the Postgres 'users' table as a Users DTO generated from the table
        schema by TestIgnite's DtoClassGenerator, assigns a task to that user through the UI by the
        row's name, and verifies the row's id landed in the 'tasks' table.
        """
    )
    fun assigningTask_toUserRowFromUsersTable(page: Page) {
        val priya = UsersTable.selectUser("u-priya")

        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Investigate flaky login")
            .openAssignDialog()
            .selectAssignee(priya.name!!)
            .confirm()
            .backToTask()
            .shouldHaveAssignee(priya.name!!)

        allureStep("Verify the task is assigned to the selected user row's id in the database") {
            TasksTable.waitForTaskAssignee("t-1", priya.id!!)
        }
    }
}
