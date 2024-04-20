package com.example.opsc7311_paradoxplanner_poe.ui.timesheet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSheetViewModel:ViewModel() {

    fun saveTimesheetEntry(startDateTime: String, endDateTime: String, category: String, description: String, photoUrl: String) {
        // TODO: Implement logic to save timesheet entry
        // For now, just log the entry details
        Log.d("TimesheetViewModel", "Saved timesheet entry: $startDateTime, $endDateTime, $category, $description, $photoUrl")
    }

}