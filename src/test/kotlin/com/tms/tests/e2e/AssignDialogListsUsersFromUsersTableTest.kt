package com.tms.tests.e2e

import com.microsoft.playwright.Page
import com.tms.db.UsersTable
import com.tms.pages.TaskBoardPage
import com.tms.tools.TmsUiExtension
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TmsUiExtension::class)
@Epic("Task Management")
@Feature("Task assignment")
class AssignDialogListsUsersFromUsersTableTest : BaseTest() {

    @Test
    @Story("Assign task")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Assign dialog offers exactly the team's users from the 'users' table")
    @Description(
        """
        The assignee dropdown is populated from the backend's 'users' table (via the gateway's
        GET /api/users → gRPC ListUsers), so it must offer exactly the team's rows — read here as
        Users DTOs generated from the table schema by TestIgnite's DtoClassGenerator.
        """
    )
    fun assignDialog_listsTheTeamUsersFromUsersTable(page: Page) {
        val falconUsers = UsersTable.selectTeamUsers("team-falcon")

        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Investigate flaky login")
            .openAssignDialog()
            .shouldOfferAssignees(falconUsers.map { it.name!! })
    }
}
