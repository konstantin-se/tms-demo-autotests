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
@Feature("Filtering")
class FilteringToMyTasksTest {

    @Test
    @Story("My tasks")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Filtering to my tasks shows only tasks assigned to the current user")
    @Description("Toggling 'Show only my tasks' hides tasks assigned to other users or left unassigned.")
    fun filteringToMyTasks_showsOnlyTasksAssignedToTheCurrentUser(page: Page) {
        TaskBoardPage(page)
            .open()
            .selectTeam("Team Falcon")
            .showOnlyMyTasks()
            .tasks()
            .shouldContainTaskTitled("Write onboarding checklist")
            .shouldNotContainTaskTitled("Patch export timeout")
            .shouldNotContainTaskTitled("Investigate flaky login")
    }
}
