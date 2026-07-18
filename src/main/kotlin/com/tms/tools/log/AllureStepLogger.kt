package com.tms.tools.log

import io.qameta.allure.listener.StepLifecycleListener
import io.qameta.allure.model.StepResult
import java.util.concurrent.atomic.AtomicInteger

// Mirrors every Allure step to SLF4J with indentation matching nesting depth, so the
// Gradle console reads as a tree. Registered via SPI at
// META-INF/services/io.qameta.allure.listener.StepLifecycleListener.
class AllureStepLogger : StepLifecycleListener {

    private val depth = ThreadLocal.withInitial { AtomicInteger(0) }

    override fun beforeStepStart(result: StepResult) {
        val prefix = "    ".repeat(depth.get().get())
        logger().info("{}[step ▶] {}", prefix, result.name.orEmpty())
    }

    override fun afterStepStart(result: StepResult) {
        depth.get().incrementAndGet()
    }

    override fun beforeStepStop(result: StepResult) {
        val newDepth = depth.get().updateAndGet { (it - 1).coerceAtLeast(0) }
        val prefix = "    ".repeat(newDepth)
        val marker = when (result.status?.value()?.lowercase()) {
            "passed", null -> "✓"
            "failed" -> "✗"
            "broken" -> "⚠"
            "skipped" -> "⤼"
            else -> "•"
        }
        logger().info("{}[step {}] {}", prefix, marker, result.name.orEmpty())
    }
}
