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
class SortingTasksByDueDateTest {

    @Test
    @Story("Sorting")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Sorting by due date orders tasks chronologically")
    @Description("Sorting Team Falcon's tasks by due date lists them earliest-due first.")
    fun sortingByDueDate_ordersTasksChronologically(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .sortBy("Due date")
            .tasks()
            .shouldListTasksInOrder(
                "Archive Q1 reports",
                "Patch export timeout",
                "Investigate flaky login",
                "Write onboarding checklist",
                "Refresh dashboard styles",
            )
    }
}
