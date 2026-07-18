package com.tms.tools.server

import com.google.protobuf.util.JsonFormat
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import com.tms.grpc.CompleteTaskRequest
import com.tms.grpc.GetTaskRequest
import com.tms.grpc.ReassignTaskRequest
import com.tms.grpc.TaskServiceGrpc
import com.tms.tools.log.logger
import io.grpc.ManagedChannelBuilder
import io.grpc.Status
import io.grpc.StatusRuntimeException
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.regex.Pattern

object StaticSiteServer {

    private val server: HttpServer = HttpServer.create(InetSocketAddress("localhost", 0), 0)

    private val taskRoute: Pattern = Pattern.compile("^/api/tasks/([^/]+)$")
    private val assigneeRoute: Pattern = Pattern.compile("^/api/tasks/([^/]+)/assignee$")
    private val statusRoute: Pattern = Pattern.compile("^/api/tasks/([^/]+)/status$")

    // Browsers can't speak native gRPC, so the webapp's fetch() calls land here and
    // are forwarded to the TaskService as real gRPC calls.
    private val taskService: TaskServiceGrpc.TaskServiceBlockingStub by lazy {
        val channel = ManagedChannelBuilder.forAddress("localhost", GrpcApiServer.port).usePlaintext().build()
        Runtime.getRuntime().addShutdownHook(Thread { runCatching { channel.shutdownNow() } })
        TaskServiceGrpc.newBlockingStub(channel)
    }

    @Volatile
    var assigneePersistenceHandler: ((taskId: String, assigneeId: String) -> Unit)? = { taskId, assigneeId ->
        taskService.reassignTask(
            ReassignTaskRequest.newBuilder().setTaskId(taskId).setAssigneeId(assigneeId).build()
        )
    }

    @Volatile
    var completionPersistenceHandler: ((taskId: String) -> Unit)? = { taskId ->
        taskService.completeTask(CompleteTaskRequest.newBuilder().setTaskId(taskId).build())
    }

    fun start(): String {
        server.createContext("/", ::handle)
        // A pool, not the single dispatch thread: the first gateway POST lazily boots the
        // gRPC server + DB container and must not starve page loads while doing so.
        server.executor = Executors.newCachedThreadPool()
        server.start()
        Runtime.getRuntime().addShutdownHook(Thread { runCatching { server.stop(0) } })
        return "http://localhost:${server.address.port}"
    }

    private fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.path
        if (exchange.requestMethod == "GET") {
            val taskMatcher = taskRoute.matcher(path)
            if (taskMatcher.matches()) {
                handleGetTask(exchange, taskMatcher.group(1))
                return
            }
        }
        if (exchange.requestMethod == "POST") {
            val assigneeMatcher = assigneeRoute.matcher(path)
            if (assigneeMatcher.matches()) {
                val handler = assigneePersistenceHandler
                handlePersistence(exchange, assigneeMatcher.group(1)) { taskId, body ->
                    handler?.invoke(taskId, body) ?: return@handlePersistence false
                    true
                }
                return
            }
            val statusMatcher = statusRoute.matcher(path)
            if (statusMatcher.matches()) {
                val handler = completionPersistenceHandler
                handlePersistence(exchange, statusMatcher.group(1)) { taskId, _ ->
                    handler?.invoke(taskId) ?: return@handlePersistence false
                    true
                }
                return
            }
        }

        val requestedPath = path.removePrefix("/").ifBlank { "index.html" }
        val resource = javaClass.classLoader.getResourceAsStream("webapp/$requestedPath")
        if (resource == null) {
            exchange.sendResponseHeaders(404, -1)
            exchange.close()
            return
        }
        resource.use { input ->
            val bytes = input.readBytes()
            exchange.responseHeaders.add("Content-Type", contentTypeFor(requestedPath))
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
    }

    // REST read side of the gateway: GET /api/tasks/{id} answers with the task as
    // the gRPC TaskService currently sees it, rendered as proto-JSON.
    private fun handleGetTask(exchange: HttpExchange, taskId: String) {
        try {
            val task = taskService.getTask(GetTaskRequest.newBuilder().setTaskId(taskId).build())
            val bytes = JsonFormat.printer().print(task).toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/json; charset=utf-8")
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        } catch (e: StatusRuntimeException) {
            if (e.status.code == Status.Code.NOT_FOUND) {
                exchange.sendResponseHeaders(404, -1)
            } else {
                logger().error("Failed to read task '{}' via gRPC", taskId, e)
                exchange.sendResponseHeaders(500, -1)
            }
        } catch (e: Exception) {
            logger().error("Failed to read task '{}' via gRPC", taskId, e)
            exchange.sendResponseHeaders(500, -1)
        } finally {
            exchange.close()
        }
    }

    private fun handlePersistence(
        exchange: HttpExchange,
        taskId: String,
        persist: (taskId: String, body: String) -> Boolean
    ) {
        val body = exchange.requestBody.use { it.readBytes().toString(Charsets.UTF_8) }
        try {
            if (persist(taskId, body)) {
                exchange.sendResponseHeaders(204, -1)
            } else {
                exchange.sendResponseHeaders(404, -1)
            }
        } catch (e: Exception) {
            logger().error("Failed to persist update for task '{}' via gRPC", taskId, e)
            exchange.sendResponseHeaders(500, -1)
        } finally {
            exchange.close()
        }
    }

    private fun contentTypeFor(path: String): String = when {
        path.endsWith(".html") -> "text/html; charset=utf-8"
        path.endsWith(".css") -> "text/css; charset=utf-8"
        path.endsWith(".js") -> "text/javascript; charset=utf-8"
        else -> "application/octet-stream"
    }
}
