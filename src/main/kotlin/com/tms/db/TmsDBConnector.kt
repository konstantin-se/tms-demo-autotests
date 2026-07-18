package com.tms.db

import io.testignite.database.DBConfig
import io.testignite.database.DBConnector
import io.testignite.database.DBSqlExecutor
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object TmsDBConnector {

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
