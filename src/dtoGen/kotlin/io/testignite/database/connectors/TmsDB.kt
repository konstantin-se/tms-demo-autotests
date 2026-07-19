package io.testignite.database.connectors

import io.testignite.database.DBConfig
import io.testignite.database.DBConnector
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * Build-time-only bootstrap for DtoClassGenerator, which resolves this object by the dbName from
 * dto_generation_config.yml and reads the static `connection` backing field reflectively — so
 * `connection` must stay a plain eager `val` (a `by lazy` field would be named `connection$delegate`).
 * Spins up a throwaway Postgres with the same init script as the test-time TmsDBConnector, purely
 * to expose the schema; Ryuk reaps the container when the generator JVM exits.
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
}
