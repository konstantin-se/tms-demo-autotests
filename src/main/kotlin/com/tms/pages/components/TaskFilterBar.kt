package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.SelectOption
import io.testignite.steps.allureStep

class TaskFilterBar(page: Page, root: Locator) : BaseComponent(page, root) {

    private val statusFilter: Locator = root.getByLabel("Status")
    private val sortSelect: Locator = root.getByLabel("Sort by")
    private val myTasksToggle: Locator = root.getByLabel("Show only my tasks")

    fun filterByStatus(status: String): TaskFilterBar = allureStep("Filter tasks by status '$status'") {
        statusFilter.selectOption(SelectOption().setLabel(status))
        this@TaskFilterBar
    }

    fun sortBy(criterion: String): TaskFilterBar = allureStep("Sort tasks by '$criterion'") {
        sortSelect.selectOption(SelectOption().setLabel(criterion))
        this@TaskFilterBar
    }

    fun showOnlyMyTasks(): TaskFilterBar = allureStep("Show only my tasks") {
        myTasksToggle.check()
        this@TaskFilterBar
    }

    fun showAllTasks(): TaskFilterBar = allureStep("Show all tasks") {
        myTasksToggle.uncheck()
        this@TaskFilterBar
    }
}
