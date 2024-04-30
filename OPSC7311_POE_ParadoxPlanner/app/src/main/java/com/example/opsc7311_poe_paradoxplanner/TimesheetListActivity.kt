package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimesheetListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var timeSheetList: ListView
    private lateinit var navEntryButton: FloatingActionButton

    companion object {
        private const val TAG = "TimesheetListActivity"
    }

    private fun loadTimesheetEntries() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("timesheets").document(userId).collection("entries")
            .get()
            .addOnSuccessListener { documents ->
                val entries = documents.map { it.toObject(TimesheetEntry::class.java) }
                setupListView(entries)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setupListView(entries: List<TimesheetEntry>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        timeSheetList.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        timeSheetList = findViewById(R.id.TimeSheetList)
        navEntryButton = findViewById(R.id.NavEntryButton)

        loadTimesheetEntries()
    }

}