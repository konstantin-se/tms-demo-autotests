package com.tms.tools.server

import com.tms.tools.server.grpc.TaskGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder

/**
 * In-process gRPC backend of the mock TMS app, exposing [TaskGrpcService] on a
 * random port. Started lazily by whoever needs it first — the static server's
 * HTTP-to-gRPC gateway or a test's gRPC client.
 */
object GrpcApiServer {

    private val server: Server by lazy {
        ServerBuilder.forPort(0)
            .addService(TaskGrpcService())
            .build()
            .start()
            .also { started ->
                Runtime.getRuntime().addShutdownHook(Thread { runCatching { started.shutdownNow() } })
            }
    }

    val port: Int get() = server.port
}
