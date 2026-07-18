package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import io.testignite.steps.allureStep

class TaskTable(page: Page, root: Locator) : BaseComponent(page, root) {

    private val rows: Locator = root.locator("tbody").getByRole(AriaRole.ROW)

    fun row(title: String): TaskRow =
        TaskRow(page, rows.filter(Locator.FilterOptions().setHasText(title)).first())

    fun shouldContainTaskTitled(title: String): TaskTable = allureStep("Verify a task titled '$title' is listed") {
        assertThat(rows.filter(Locator.FilterOptions().setHasText(title)).first()).isVisible()
        this@TaskTable
    }

    fun shouldNotContainTaskTitled(title: String): TaskTable = allureStep("Verify no task titled '$title' is listed") {
        assertThat(rows.filter(Locator.FilterOptions().setHasText(title))).hasCount(0)
        this@TaskTable
    }

    fun shouldListTasksInOrder(vararg titles: String): TaskTable =
        allureStep("Verify tasks are listed in order: ${titles.joinToString()}") {
            val actualTitles = rows.all().map { it.locator("td").first().innerText() }
            check(actualTitles == titles.toList()) { "Expected order $titles but was $actualTitles" }
            this@TaskTable
        }
}
