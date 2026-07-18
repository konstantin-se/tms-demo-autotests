package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import io.testignite.steps.allureStep
import java.util.regex.Pattern

class TaskRow(page: Page, root: Locator) : BaseComponent(page, root) {

    private val statusBadge: Locator = find(".status-badge")
    private val assigneeCell: Locator = find(".assignee-cell")
    private val viewButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName(Pattern.compile("^View details for")))
    private val assignButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName(Pattern.compile("^(Assign|Reassign) ")))
    private val completeButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName(Pattern.compile("^Mark .* as complete$")))

    fun openDetails(): TaskDetailDialog = allureStep("Open task details") {
        viewButton.click()
        TaskDetailDialog(page, page.locator("#detail-dialog"), this@TaskRow)
    }

    fun openAssignDialog(): AssignTaskDialog = allureStep("Open the assign dialog") {
        assignButton.click()
        AssignTaskDialog(page, page.locator("#assign-dialog"), this@TaskRow)
    }

    fun markComplete(): TaskRow = allureStep("Mark the task as complete") {
        completeButton.click()
        this@TaskRow
    }

    fun shouldHaveStatus(expected: String): TaskRow = allureStep("Verify status is '$expected'") {
        assertThat(statusBadge).hasText(expected)
        this@TaskRow
    }

    fun shouldHaveAssignee(expected: String): TaskRow = allureStep("Verify assignee is '$expected'") {
        assertThat(assigneeCell).hasText(expected)
        this@TaskRow
    }

    fun shouldHaveReassignDisabled(): TaskRow = allureStep("Verify the assign/reassign button is disabled") {
        assertThat(assignButton).isDisabled()
        this@TaskRow
    }
}
