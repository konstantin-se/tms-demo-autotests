package com.tms.app

import com.tms.db.TmsDBConnector
import com.tms.tools.server.GrpcApiServer
import com.tms.tools.server.StaticSiteServer

fun main() {
    TmsDBConnector.connection
    val url = StaticSiteServer.start()
    println("TMS mock app running at $url (gRPC TaskService on localhost:${GrpcApiServer.port}) — press Enter to stop.")
    readLine()
}
