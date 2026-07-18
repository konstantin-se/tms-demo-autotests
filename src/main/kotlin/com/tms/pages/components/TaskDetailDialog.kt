package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import io.testignite.steps.allureStep

class TaskDetailDialog(
    page: Page,
    root: Locator,
    private val sourceRow: TaskRow,
) : BaseComponent(page, root) {

    private val titleHeading: Locator = root.getByRole(AriaRole.HEADING)
    private val description: Locator = find("#detail-description")
    private val status: Locator = find("#detail-status")
    private val priority: Locator = find("#detail-priority")
    private val dueDate: Locator = find("#detail-due-date")
    private val assignee: Locator = find("#detail-assignee")
    private val closeButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Close"))

    fun shouldShowTitle(expected: String): TaskDetailDialog = allureStep("Verify title is '$expected'") {
        assertThat(titleHeading).hasText(expected)
        this@TaskDetailDialog
    }

    fun shouldShowDescription(expected: String): TaskDetailDialog = allureStep("Verify description is '$expected'") {
        assertThat(description).hasText(expected)
        this@TaskDetailDialog
    }

    fun shouldShowStatus(expected: String): TaskDetailDialog = allureStep("Verify status is '$expected'") {
        assertThat(status).hasText(expected)
        this@TaskDetailDialog
    }

    fun shouldShowPriority(expected: String): TaskDetailDialog = allureStep("Verify priority is '$expected'") {
        assertThat(priority).hasText(expected)
        this@TaskDetailDialog
    }

    fun shouldShowDueDate(expected: String): TaskDetailDialog = allureStep("Verify due date is '$expected'") {
        assertThat(dueDate).hasText(expected)
        this@TaskDetailDialog
    }

    fun shouldShowAssignee(expected: String): TaskDetailDialog = allureStep("Verify assignee is '$expected'") {
        assertThat(assignee).hasText(expected)
        this@TaskDetailDialog
    }

    fun close(): TaskRow = allureStep("Close the task details") {
        closeButton.click()
        sourceRow
    }
}
