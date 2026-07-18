package com.tms.models


data class TaskDbRecord(
    val id: String = "",
    val title: String = "",
    val status: String = "",
    val assigneeId: String = ""
)