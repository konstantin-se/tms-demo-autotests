package com.tms.tests.grpc_api

import com.tms.api.TaskServiceApi
import com.tms.db.UsersTable
import com.tms.tools.shouldBe
import io.qameta.allure.*
import io.testignite.basetest.BaseTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Epic("Task Management")
@Feature("Task API")
class ListingUsersViaGrpcApiTest : BaseTest() {

    @Test
    @Story("List users")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Listing users through the gRPC TaskService returns every row of the 'users' table")
    @Description(
        """
        ListUsers on the gRPC TaskService must mirror the Postgres 'users' table row for row,
        compared against Users DTOs generated from the table schema by TestIgnite's
        DtoClassGenerator.
        """
    )
    fun listingUsersViaGrpc_returnsEveryUserRow() {
        val expected = UsersTable.selectAllUsers()
            .joinToString("\n") { "${it.id} | ${it.name} | ${it.teamId}" }

        TaskServiceApi.listUsers().usersList
            .joinToString("\n") { "${it.id} | ${it.name} | ${it.teamId}" }
            .shouldBe(expected, "users returned by ListUsers")
    }
}
