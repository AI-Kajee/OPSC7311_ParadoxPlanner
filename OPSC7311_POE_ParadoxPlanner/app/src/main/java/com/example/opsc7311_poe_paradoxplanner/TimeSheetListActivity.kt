package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlin.math.roundToInt

class TimeSheetListActivity : AppCompatActivity(), TimeSheetAdapter.OnItemClickListener {
    private lateinit var timeSheetRecyclerView: RecyclerView
    private lateinit var entryArrayList: ArrayList<TimesheetEntry>
    private lateinit var timeSheetAdapter: TimeSheetAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnAddTimeSheetButton: ImageButton
    private lateinit var timesheetSeekBar: SeekBar
    private lateinit var seekbarValueDisplay : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sheet_list)

        timeSheetRecyclerView = findViewById(R.id.timeSheetRecyclerView)
        timeSheetRecyclerView.layoutManager = LinearLayoutManager(this)
        timeSheetRecyclerView.setHasFixedSize(true)

        timesheetSeekBar = findViewById(R.id.timesheetSeekBar)

        seekbarValueDisplay=findViewById(R.id.tvSeekbarValue)

        entryArrayList = arrayListOf()

        timeSheetAdapter = TimeSheetAdapter(entryArrayList,100.0)

        timeSheetRecyclerView.adapter = timeSheetAdapter

        btnAddTimeSheetButton = findViewById(R.id.AddTimesheetButton)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser!= null) {
            val userId = currentUser.uid
            eventChangeListner(userId)
        } else {
            Log.e("User Error", "Current user is null")
        }

        // Set the item click listener
        timeSheetAdapter.setOnItemClickListener(this)

        // Set up SeekBar listener
        timesheetSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Convert the SeekBar's progress to a Double representing the timesheet durations
                val targetDurations = progress.toDouble()

                // Display the current SeekBar value
                seekbarValueDisplay.text="$targetDurations"

                // Fetch timesheets from Firestore where targetDurations equals the SeekBar's value
                fetchTimesheets(targetDurations)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle when the user starts moving the SeekBar
                Toast.makeText(this@TimeSheetListActivity, "Started moving SeekBar", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle when the user stops moving the SeekBar
                Toast.makeText(this@TimeSheetListActivity, "Stopped moving SeekBar", Toast.LENGTH_SHORT).show()
            }
        })

    }




    private fun fetchTimesheets(targetDuration: Double) {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser!= null) {
            val query = db.collection("timesheet")
                .whereEqualTo("userId", currentUser.uid) // Filter timesheets by the current user's ID

            query.addSnapshotListener { value, error ->
                if (error!= null) {
                    // Handle error
                    return@addSnapshotListener
                }

                value?.let { snapshot ->
                    entryArrayList.clear()
                    for (document in snapshot.documents) {
                        val timesheet = document.toObject(TimesheetEntry::class.java)
                        timesheet?.let { entry ->
                            // Attempt to parse the duration string to a Double
                            val durationAsDouble = entry.duration?.toDouble()

                            if (durationAsDouble!= null && durationAsDouble.roundToInt() == targetDuration.roundToInt()) {
                                entryArrayList.add(entry)
                            }

                            else if (durationAsDouble!= null && targetDuration==100.0 && durationAsDouble > 100) {
                                entryArrayList.add(entry)
                            }

                            else{
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }

                        }
                    }
                    timeSheetAdapter.notifyDataSetChanged()
                }
            }
        } else {
            // Handle case where no user is logged in
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }






















    private fun eventChangeListner(userId: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("timesheet")
            .whereEqualTo("userId", userId)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error!= null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    entryArrayList.clear() // Clear the existing list to avoid duplicates
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.MODIFIED) {
                            val entry = dc.document.toObject(TimesheetEntry::class.java)
                            entryArrayList.add(entry)
                        }
                    }
                    timeSheetAdapter.notifyDataSetChanged()
                }
            })

        btnAddTimeSheetButton.setOnClickListener {
            val intent = Intent(this, TimesheetActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    override fun onItemClick(position: Int) {
        val selectedEntry = entryArrayList[position]
    }
}
