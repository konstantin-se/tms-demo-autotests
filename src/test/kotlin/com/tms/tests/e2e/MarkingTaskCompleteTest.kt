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
class MarkingTaskCompleteTest {

    @Test
    @Story("Task status")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Marking a task complete updates its status")
    @Description("Clicking 'Mark complete' on an open task updates its status badge to Done.")
    fun markingATaskComplete_updatesItsStatus(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Nova")
            .tasks()
            .row("Tune search relevance")
            .shouldHaveStatus("Open")
            .markComplete()
            .shouldHaveStatus("Done")
    }
}
