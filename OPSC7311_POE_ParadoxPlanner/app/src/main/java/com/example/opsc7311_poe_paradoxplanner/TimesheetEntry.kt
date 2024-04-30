package com.example.opsc7311_poe_paradoxplanner

data class TimesheetEntry(
    val category: String,
    val description: String,
    val endDate: String,
    val endTime : String,
    val photoUrl: String,
    val startDate: String,
    val startTime : String,
    val timesheetName: String
)
