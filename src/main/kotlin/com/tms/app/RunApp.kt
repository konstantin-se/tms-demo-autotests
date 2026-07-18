package com.tms.app

import com.tms.tools.server.StaticSiteServer

fun main() {
    val url = StaticSiteServer.start()
    println("TMS mock app running at $url — press Enter to stop.")
    readLine()
}
