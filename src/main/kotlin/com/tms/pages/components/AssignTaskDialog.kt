package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.AriaRole
import com.microsoft.playwright.options.SelectOption
import io.testignite.steps.allureStep

class AssignTaskDialog(
    page: Page,
    root: Locator,
    private val sourceRow: TaskRow,
) : BaseComponent(page, root) {

    private val assigneeSelect: Locator = root.getByLabel("Assignee")
    private val confirmButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Confirm assignment"))
    private val cancelButton: Locator =
        root.getByRole(AriaRole.BUTTON, Locator.GetByRoleOptions().setName("Cancel"))
    private val errorMessage: Locator = root.getByRole(AriaRole.ALERT)

    fun selectAssignee(name: String): AssignTaskDialog = allureStep("Select assignee '$name'") {
        assigneeSelect.selectOption(SelectOption().setLabel(name))
        this@AssignTaskDialog
    }

    fun confirm(): AssignTaskDialog = allureStep("Confirm the assignment") {
        confirmButton.click()
        this@AssignTaskDialog
    }

    fun shouldShowValidationError(): AssignTaskDialog = allureStep("Verify a validation error is shown") {
        assertThat(errorMessage).isVisible()
        this@AssignTaskDialog
    }

    fun shouldOfferAssignees(names: List<String>): AssignTaskDialog =
        allureStep("Verify the assignee options are exactly $names") {
            assertThat(assigneeSelect.locator("option"))
                .hasText((listOf("-- Select a user --") + names).toTypedArray())
            this@AssignTaskDialog
        }

    fun cancel(): TaskRow = allureStep("Cancel the assignment") {
        cancelButton.click()
        sourceRow
    }

    fun backToTask(): TaskRow = allureStep("Return to the task row") {
        assertThat(root).not().isVisible()
        sourceRow
    }
}
