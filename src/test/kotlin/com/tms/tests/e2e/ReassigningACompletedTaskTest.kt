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
@Feature("Task assignment")
class ReassigningACompletedTaskTest {

    @Test
    @Story("Reassign task")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A completed task cannot be reassigned")
    @Description("A task with status Done has its reassign control disabled, and its assignee stays unchanged.")
    fun reassigningACompletedTask_isBlocked(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Archive Q1 reports")
            .shouldHaveStatus("Done")
            .shouldHaveReassignDisabled()
            .shouldHaveAssignee("Priya Nair")
    }
}
