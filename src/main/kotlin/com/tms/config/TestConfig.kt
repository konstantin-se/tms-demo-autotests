package com.tms.config

import com.tms.tools.server.StaticSiteServer

object TestConfig {
    val baseUrl: String by lazy { StaticSiteServer.start() }
    val defaultTimeoutMs: Long = System.getProperty("timeoutMs", "10000").toLong()
    val headed: Boolean = System.getProperty("headed", "true").toBoolean()
    val slowMoMs: Double = System.getProperty("slowMo", "0").toDouble()
}
