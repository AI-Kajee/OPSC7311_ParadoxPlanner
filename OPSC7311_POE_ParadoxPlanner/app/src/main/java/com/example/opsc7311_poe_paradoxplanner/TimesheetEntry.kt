package com.example.opsc7311_poe_paradoxplanner

data class TimesheetEntry(
    val category: String ?= null,
    val description: String ?= null,
    val endDate: String ?= null,
    val endTime : String ?= null,
    val photoUrl: String ?= null,
    val startDate: String ?= null,
    val startTime : String ?= null,
    val timesheetName: String ?= null
)
