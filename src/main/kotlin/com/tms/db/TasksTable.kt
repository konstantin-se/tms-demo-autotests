package com.tms.db

import com.tms.models.TaskDbRecord
import io.testignite.steps.allureStep

object TasksTable {

    private val tmsDB get() = TmsDBConnector.sqlExecutor

    fun insertTask(id: String, title: String, status: String): Unit =
        allureStep("DB: insert task '$id' ('$title', $status, unassigned)") {
            tmsDB.insert(
                """
                INSERT INTO tasks (id, title, status, assignee_id)
                VALUES ('$id', '$title', '$status', NULL)
                """.trimIndent()
            )
        }

    fun updateTaskAssignee(taskId: String, assigneeId: String): Unit =
        tmsDB.update("UPDATE tasks SET assignee_id = '$assigneeId' WHERE id = '$taskId'")


    fun waitForTaskAssignee(taskId: String, assigneeId: String): Unit =
        allureStep("DB: wait until task '$taskId' has assignee '$assigneeId'") {
            tmsDB.selectWithWait(
                5, 1,
                """
                SELECT assignee_id FROM tasks
                WHERE id = '$taskId' AND assignee_id = '$assigneeId'
                """.trimIndent()
            )
        }

    fun selectTask(taskId: String): TaskDbRecord =
        allureStep("DB: select task '$taskId'") {
            tmsDB.selectObject(
                """
                SELECT id, title, status, assignee_id FROM tasks
                WHERE id = '$taskId'
                """.trimIndent(),
                TaskDbRecord::class.java
            )
        }

    fun deleteAllTasks(): Unit =
        allureStep("DB: delete all tasks") {
            tmsDB.delete("DELETE FROM tasks")
        }
}

