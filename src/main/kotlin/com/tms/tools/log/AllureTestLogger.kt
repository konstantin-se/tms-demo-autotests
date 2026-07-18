package com.tms.tools.log

import io.qameta.allure.listener.TestLifecycleListener
import io.qameta.allure.model.TestResult

// Mirrors Allure test lifecycle events to SLF4J so console output cleanly delimits each
// test. Registered via SPI at META-INF/services/io.qameta.allure.listener.TestLifecycleListener.
class AllureTestLogger : TestLifecycleListener {

    override fun afterTestStart(result: TestResult) {
        logger().info("===== [test ▶] {} =====", result.name.orEmpty())
    }

    override fun afterTestStop(result: TestResult) {
        val status = result.status?.value()?.uppercase() ?: "UNKNOWN"
        logger().info("===== [test ■ {}] {} =====", status, result.name.orEmpty())
    }
}
