package io.testignite.database.connectors

import io.testignite.database.DBConfig
import io.testignite.database.DBConnector
import io.testignite.database.DBSqlExecutor
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * The project's single DB connector, owning the lazy-started Testcontainers Postgres. It lives in
 * this package (not com.tms.db) because DtoClassGenerator resolves it by the dbName from
 * dto_generation_config.yml and reads the static `connection` backing field reflectively — so
 * `connection` must stay a plain eager `val` (a `by lazy` field would be named `connection$delegate`).
 * Main code reaches it as com.tms.db.TmsDBConnector (a typealias); at build time the generator
 * loads the same class from the dtoGen source set to read the schema.
 */
object TmsDB {

    private val postgres: PostgreSQLContainer<Nothing> =
        PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16-alpine"))
            .withInitScript("db/init-tasks.sql")
            .apply { start() }

    val connection = DBConnector(
        DBConfig(
            postgres.jdbcUrl,
            postgres.username,
            postgres.password,
            "public"
        )
    )

    val sqlExecutor = DBSqlExecutor(connection)
}
