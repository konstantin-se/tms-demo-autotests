package com.tms.pages

import com.tms.config.TestConfig
import com.microsoft.playwright.Page
import io.testignite.steps.allureStep

abstract class BasePage(protected val page: Page) {

    init {
        page.setDefaultTimeout(TestConfig.defaultTimeoutMs.toDouble())
    }

    protected fun open(path: String) = allureStep("Open '$path'") {
        page.navigate(TestConfig.baseUrl + path)
        page.waitForLoadState()
    }
}
