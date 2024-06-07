package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimerActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var timeSheetSpinner: Spinner
    private lateinit var btnTimer: Button
    private lateinit var btnBack: Button

    private var isTimerRunning = false // Tracks if the timer is running
    private var startTime: Long = 0 // Stores the start time

    companion object {
        private const val TAG = "TimerActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timer)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        timeSheetSpinner = findViewById(R.id.timeSheetSpinner)
        btnBack = findViewById(R.id.btnBack)
        btnTimer = findViewById(R.id.btnTimer)


        // Fetch timesheets from Firestore and populate the spinner
        val userId = auth.currentUser?.uid
        if (userId!= null) {
            db.collection("timesheet").whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val timesheets = querySnapshot.documents.map { it.getString("timesheetName")!! }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timesheets)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    timeSheetSpinner.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d(TimerActivity.TAG, "Error fetching timesheets: ", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }




        btnTimer.setOnClickListener { view ->
            if (!isTimerRunning) {
                // Store the first click time
                startTime = SystemClock.elapsedRealtime()
                isTimerRunning = true

                Toast.makeText(view.context, "First Timer Click", Toast.LENGTH_SHORT).show()

                Log.d(TAG, "First Timer Click")

            } else {
                val currentTime = SystemClock.elapsedRealtime()
                val elapsedTimeMs = currentTime - startTime
                val elapsedTimeSec = elapsedTimeMs / 1000
                val elapsedTimeMin = elapsedTimeSec / 60
                val elapsedTimeHours = elapsedTimeMin / 60
                val roundedElapsedTimeHours = String.format("%.2f", elapsedTimeHours)
                val roundedElapsedTimeMin = String.format("%.2f", elapsedTimeMin % 60)


                Toast.makeText(view.context, "Elapsed Time: $elapsedTimeMs ms",Toast.LENGTH_SHORT).show()

                // Display the elapsed time or perform other actions with it
                Log.d(TAG, "Elapsed Time: $elapsedTimeMs ms")

                // Reset the timer state
                isTimerRunning = false

                startTime = 0

                Toast.makeText(view.context, "Second Timer Click", Toast.LENGTH_SHORT).show()

                Log.d(TAG, "Second Timer Click")
            }
        }


    }

}