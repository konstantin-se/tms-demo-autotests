package com.tms.pages.components

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.options.SelectOption
import io.testignite.steps.allureStep

class TeamSelector(page: Page, root: Locator) : BaseComponent(page, root) {

    fun select(teamName: String): TeamSelector = allureStep("Select team '$teamName'") {
        root.selectOption(SelectOption().setLabel(teamName))
        this@TeamSelector
    }
}
