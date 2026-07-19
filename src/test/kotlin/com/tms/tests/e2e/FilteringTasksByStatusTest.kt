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
class FilteringTasksByStatusTest {

    @Test
    @Story("Filtering")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Filtering by status shows only matching tasks")
    @Description("Filtering the task list to 'Done' hides Open and In Progress tasks for the selected team.")
    fun filteringByStatus_showsOnlyMatchingTasks(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .filterByStatus("Done")
            .tasks()
            .shouldContainTaskTitled("Archive Q1 reports")
            .shouldNotContainTaskTitled("Investigate flaky login")
            .shouldNotContainTaskTitled("Patch export timeout")
    }
}
