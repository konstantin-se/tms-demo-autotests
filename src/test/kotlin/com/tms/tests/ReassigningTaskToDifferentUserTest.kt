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
class ReassigningTaskToDifferentUserTest: BaseTest() {

    @Test
    @Story("Reassign task")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Reassigning a task changes the assignee")
    @Description("Opening the assign dialog on an already-assigned task and confirming a different user replaces the assignee.")
    fun reassigningTask_changesTheAssignee(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .tasks()
            .row("Patch export timeout")
            .shouldHaveAssignee("Marcus Lee")
            .openAssignDialog()
            .selectAssignee("Priya Nair")
            .confirm()
            .backToTask()
            .shouldHaveAssignee("Priya Nair")
    }
}
