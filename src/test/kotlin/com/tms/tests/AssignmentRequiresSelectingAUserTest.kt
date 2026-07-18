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
import io.testignite.basetest.BaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TmsUiExtension::class)
@Epic("Task Management")
@Feature("Task assignment")
class AssignmentRequiresSelectingAUserTest : BaseTest() {

    @Test
    @Story("Assign task")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Confirming assignment without a user shows a validation error")
    @Description("Confirming the assign dialog while no user is selected shows a validation error and leaves the task unassigned.")
    fun confirmingWithoutSelectingAUser_showsValidationError(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Refresh dashboard styles")
            .openAssignDialog()
            .confirm()
            .shouldShowValidationError()
            .cancel()
            .shouldHaveAssignee("Unassigned")
    }
}
