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
class ViewingTaskDetailsTest {

    @Test
    @Story("Task details")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Viewing a task shows its full details")
    @Description("Opening the detail dialog for a task shows its description, status, priority, due date and assignee.")
    fun viewingATask_showsItsFullDetails(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Orbit")
            .tasks()
            .row("Draft release notes")
            .openDetails()
            .shouldShowTitle("Draft release notes")
            .shouldShowDescription("Summarize this sprint's changes.")
            .shouldShowStatus("In Progress")
            .shouldShowPriority("Medium")
            .shouldShowDueDate("2026-07-18")
            .shouldShowAssignee("Sofia Kim")
            .close()
            .shouldHaveStatus("In Progress")
    }
}
