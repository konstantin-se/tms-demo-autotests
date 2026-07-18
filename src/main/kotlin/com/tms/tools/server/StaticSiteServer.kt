package com.tms.tools.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import com.tms.tools.log.logger
import java.net.InetSocketAddress
import java.util.regex.Pattern

object StaticSiteServer {

    private val server: HttpServer = HttpServer.create(InetSocketAddress("localhost", 0), 0)

    private val assigneeRoute: Pattern = Pattern.compile("^/api/tasks/([^/]+)/assignee$")

    // Nullable, settable persistence hook for POST /api/tasks/{id}/assignee. Left null by default so
    // every other test and `runApp` gets a 404 on that route — only tests that opt in (by setting this
    // from a test class) get real persistence behavior. Plain function type: TestIgnite is test-only,
    // not on the `main` sourceset's compile classpath, so the DB-writing lambda is built in the test
    // class and only crosses into main code as an opaque function value.
    //
    // This is JVM-wide mutable state on a singleton `object`, which is only safe because this repo
    // runs tests sequentially (no parallel execution configured anywhere). If parallel test execution
    // is ever introduced, this hook design needs revisiting (e.g. per-context routing) so one test's
    // fetch calls don't hit another test's DB handler.
    @Volatile
    var assigneePersistenceHandler: ((taskId: String, assigneeId: String) -> Unit)? = null

    fun start(): String {
        server.createContext("/", ::handle)
        server.executor = null
        server.start()
        Runtime.getRuntime().addShutdownHook(Thread { runCatching { server.stop(0) } })
        return "http://localhost:${server.address.port}"
    }

    private fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.path
        val assigneeMatcher = assigneeRoute.matcher(path)
        if (exchange.requestMethod == "POST" && assigneeMatcher.matches()) {
            handleAssigneeUpdate(exchange, assigneeMatcher.group(1))
            return
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

    private fun handleAssigneeUpdate(exchange: HttpExchange, taskId: String) {
        val handler = assigneePersistenceHandler
        if (handler == null) {
            exchange.sendResponseHeaders(404, -1)
            exchange.close()
            return
        }
        val assigneeId = exchange.requestBody.use { it.readBytes().toString(Charsets.UTF_8) }
        try {
            handler(taskId, assigneeId)
            exchange.sendResponseHeaders(204, -1)
        } catch (e: Exception) {
            logger().error("Failed to persist assignee update for task '{}'", taskId, e)
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
