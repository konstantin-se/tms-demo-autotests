package com.tms.tools.driver

import com.tms.config.TestConfig
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright

object BrowserFactory {

    private val playwright: Playwright = Playwright.create()

    val browser: Browser = playwright.chromium().launch(
        BrowserType.LaunchOptions()
            .setHeadless(!TestConfig.headed)
            .setSlowMo(TestConfig.slowMoMs)
    )

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            runCatching { browser.close() }
            runCatching { playwright.close() }
        })
    }
}
