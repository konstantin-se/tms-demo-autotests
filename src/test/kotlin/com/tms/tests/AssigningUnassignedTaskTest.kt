package com.tms.tests

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
@Feature("Task assignment")
class AssigningUnassignedTaskTest {

    @Test
    @Story("Assign task")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Assigning an unassigned task sets the chosen user as assignee")
    @Description("Opening the assign dialog on an unassigned task and confirming a user sets that user as assignee.")
    fun assigningUnassignedTask_setsTheChosenUserAsAssignee(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Investigate flaky login")
            .shouldHaveAssignee("Unassigned")
            .openAssignDialog()
            .selectAssignee("Priya Nair")
            .confirm()
            .backToTask()
            .shouldHaveAssignee("Priya Nair")
    }
}
