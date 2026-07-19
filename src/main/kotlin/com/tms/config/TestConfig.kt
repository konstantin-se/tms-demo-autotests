package com.tms.config

import com.tms.tools.server.StaticSiteServer
import java.net.URI

object TestConfig {
    val baseUrl: String by lazy {
        val url = StaticSiteServer.start()
        // Page load fetches /api/users, which needs the whole gateway → gRPC → Postgres stack.
        // One real request warms it all in parallel with browser startup, so the first test's
        // page doesn't pay the cold boot inside an assertion timeout.
        Thread { runCatching { URI("$url/api/users").toURL().readText() } }
            .apply { isDaemon = true }
            .also { it.start() }
        url
    }
    val defaultTimeoutMs: Long = System.getProperty("timeoutMs", "10000").toLong()
    val headed: Boolean = System.getProperty("headed", "false").toBoolean()
    val slowMoMs: Double = System.getProperty("slowMo", "0").toDouble()
}
