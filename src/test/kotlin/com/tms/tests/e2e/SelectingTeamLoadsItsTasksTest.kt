package com.tms.tests.e2e

import com.tms.tools.junit.TmsUiExtension
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
class SelectingTeamLoadsItsTasksTest {

    @Test
    @Story("Team selection")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Selecting a team loads its tasks")
    @Description("Before a team is chosen the board prompts for one; selecting a team shows only that team's tasks.")
    fun selectingTeam_loadsItsTasks(page: Page) {
        TaskBoardPage(page)
            .open()
            .shouldPromptToSelectATeam()
            .selectTeam("Team Falcon")
            .tasks()
            .shouldContainTaskTitled("Investigate flaky login")
            .shouldContainTaskTitled("Refresh dashboard styles")
            .shouldNotContainTaskTitled("Rotate API credentials")
    }
}
