package com.tms.tools

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.tms.config.TestConfig
import com.tms.tools.driver.BrowserFactory
import io.qameta.allure.Allure
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.io.ByteArrayInputStream

class TmsUiExtension : AfterEachCallback, ParameterResolver {

    // One context/page (= one browser window) shared across the whole run: the app holds all its
    // state in per-page-load JS, so each test's open() navigation resets it — no need to pay a
    // window teardown/startup per test. The shutdown hook in BrowserFactory closes everything.
    private companion object {
        val browserContext: BrowserContext by lazy {
            BrowserFactory.browser.newContext(
                Browser.NewContextOptions().setBaseURL(TestConfig.baseUrl)
            )
        }
        val page: Page by lazy { browserContext.newPage() }
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == Page::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
        page

    override fun afterEach(context: ExtensionContext) {
        runCatching {
            Allure.addAttachment("screenshot", "image/png", ByteArrayInputStream(page.screenshot()), ".png")
            Allure.addAttachment("page.html", "text/html", page.content(), ".html")
        }
    }
}