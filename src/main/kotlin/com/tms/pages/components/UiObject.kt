package com.tms.pages.components

import com.tms.config.TestConfig
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat
import com.microsoft.playwright.options.WaitForSelectorState

abstract class UiObject(protected val page: Page, protected val root: Locator) {

    fun isVisible(): Boolean = root.isVisible()

    fun isVisibleWithin(timeoutMs: Double): Boolean =
        runCatching {
            root.waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs))
            true
        }.getOrElse { false }

    fun textContent(): String = root.innerText()

    protected fun find(selector: String): Locator = root.locator(selector)

    protected fun waitVisible(timeoutMs: Double = TestConfig.defaultTimeoutMs.toDouble()): Locator {
        root.waitFor(Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs))
        return root
    }

    protected fun expectVisible() {
        assertThat(root).isVisible()
    }
}
