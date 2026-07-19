package com.tms.tests.e2e

import com.tms.tools.TmsUiExtension
import com.tms.pages.TaskBoardPage
import com.microsoft.playwright.Page
import io.qameta.allure.Description
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.Story
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TmsUiExtension::class)
@Epic("Task Management")
@Feature("Task board")
class SwitchingTeamsRefreshesTaskListTest {

    @Test
    @Story("Team selection")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Switching teams refreshes the task list")
    @Description("Changing the team selector drops the previous team's tasks and shows the new team's tasks.")
    fun switchingTeams_refreshesTaskList(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .shouldContainTaskTitled("Investigate flaky login")

        TaskBoardPage(page)
            .selectTeam("Team Orbit")
            .tasks()
            .shouldContainTaskTitled("Rotate API credentials")
            .shouldNotContainTaskTitled("Investigate flaky login")
    }
}
