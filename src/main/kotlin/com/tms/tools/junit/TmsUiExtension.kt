package com.tms.tools.junit

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

    private val namespace = ExtensionContext.Namespace.create(TmsUiExtension::class.java)

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == Page::class.java

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val browserContext = BrowserFactory.browser.newContext(
            Browser.NewContextOptions().setBaseURL(TestConfig.baseUrl)
        )
        val page = browserContext.newPage()
        val store = extensionContext.getStore(namespace)
        store.put(BrowserContext::class.java, browserContext)
        store.put(Page::class.java, page)
        return page
    }

    override fun afterEach(context: ExtensionContext) {
        val store = context.getStore(namespace)
        val page = store.get(Page::class.java) as? Page

        if (page != null) {
            runCatching {
                Allure.addAttachment("screenshot", "image/png", ByteArrayInputStream(page.screenshot()), ".png")
                Allure.addAttachment("page.html", "text/html", page.content(), ".html")
            }
        }

        (store.get(BrowserContext::class.java) as? BrowserContext)?.close()
    }
}
