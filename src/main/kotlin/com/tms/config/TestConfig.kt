package com.tms.config

import com.tms.db.TmsDBConnector
import com.tms.tools.server.StaticSiteServer

object TestConfig {
    val baseUrl: String by lazy {
        // Page load now fetches /api/users, which needs the gRPC backend and its Postgres —
        // boot them in parallel with the browser instead of inside the first test's timeout.
        Thread { runCatching { TmsDBConnector.connection } }
            .apply { isDaemon = true }
            .also { it.start() }
        StaticSiteServer.start()
    }
    val defaultTimeoutMs: Long = System.getProperty("timeoutMs", "10000").toLong()
    val headed: Boolean = System.getProperty("headed", "true").toBoolean()
    val slowMoMs: Double = System.getProperty("slowMo", "0").toDouble()
}
