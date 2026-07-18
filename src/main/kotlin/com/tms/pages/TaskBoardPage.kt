package com.tms.pages

import com.tms.pages.components.TaskFilterBar
import com.tms.pages.components.TaskTable
import com.tms.pages.components.TeamSelector
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import io.testignite.steps.allureStep

class TaskBoardPage(page: Page) : BasePage(page) {

    private val teamSelector = TeamSelector(page, page.getByLabel("Team"))
    private val filterBar = TaskFilterBar(page, page.locator("#filter-bar"))
    private val taskTable = TaskTable(page, page.getByRole(AriaRole.TABLE))
    private val emptyState = page.locator("#empty-state")

    fun open(): TaskBoardPage = allureStep("Open the TMS task board") {
        open("/")
        this
    }

    fun selectTeam(teamName: String): TaskBoardPage = allureStep("Select team '$teamName'") {
        teamSelector.select(teamName)
        this
    }

    fun filterByStatus(status: String): TaskBoardPage = allureStep("Filter tasks by status '$status'") {
        filterBar.filterByStatus(status)
        this
    }

    fun sortBy(criterion: String): TaskBoardPage = allureStep("Sort tasks by '$criterion'") {
        filterBar.sortBy(criterion)
        this
    }

    fun showOnlyMyTasks(): TaskBoardPage = allureStep("Show only my tasks") {
        filterBar.showOnlyMyTasks()
        this
    }

    fun tasks(): TaskTable = taskTable

    fun shouldPromptToSelectATeam(): TaskBoardPage = allureStep("Verify the board prompts to select a team") {
        assertThat(emptyState).hasText("Select a team to view its tasks.")
        this
    }
}
