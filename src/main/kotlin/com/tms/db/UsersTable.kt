package com.tms.db

import io.testignite.steps.allureStep
import io.testignite.tables.TmsDB.Users

object UsersTable {

    private val tmsDB get() = TmsDBConnector.sqlExecutor

    fun selectAllUsers(): List<Users> =
        allureStep("DB: select all users") {
            tmsDB.selectListObjects("SELECT id, name, team_id FROM users ORDER BY id", Users::class.java)
        }

    fun selectUser(userId: String): Users =
        allureStep("DB: select user '$userId'") {
            tmsDB.selectObject("SELECT id, name, team_id FROM users WHERE id = '$userId'", Users::class.java)
        }

    fun selectTeamUsers(teamId: String): List<Users> =
        allureStep("DB: select users of team '$teamId'") {
            tmsDB.selectListObjects(
                "SELECT id, name, team_id FROM users WHERE team_id = '$teamId' ORDER BY id",
                Users::class.java
            )
        }
}
