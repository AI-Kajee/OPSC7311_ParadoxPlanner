package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
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
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        private const val TIMER_PREFS = "timerPrefs"
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
                    val timesheets = querySnapshot.documents.map { it.getString("timesheetName")!!}
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









        // Load saved start time and running state from SharedPreferences
        val sharedPreferences = getSharedPreferences(TIMER_PREFS, MODE_PRIVATE)
        startTime = sharedPreferences.getLong("startTime", 0L)
        isTimerRunning = sharedPreferences.getBoolean("isTimerRunning", false)


        btnTimer.setOnClickListener {
            if (!isTimerRunning) {
                // Start the timer
                startTime = SystemClock.elapsedRealtime()
                isTimerRunning = true
                Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()

                // Save start time and running state to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putLong("startTime", startTime)
                editor.putBoolean("isTimerRunning", isTimerRunning)
                editor.apply()

            } else {
                // Stop the timer and calculate elapsed time
                val currentTime = SystemClock.elapsedRealtime()
                val elapsedTimeMs = BigDecimal(currentTime - startTime)
                val elapsedTimeSec = elapsedTimeMs.divide(BigDecimal(1000), MathContext.DECIMAL64)
                val elapsedTimeMin = elapsedTimeSec.divide(BigDecimal(60), MathContext.DECIMAL64)
                val elapsedTimeHours = elapsedTimeMin.divide(BigDecimal(60), MathContext.DECIMAL64)
                val timesheetName = timeSheetSpinner.selectedItem.toString()
               /* val elapsedTimeMs = currentTime - startTime
                val elapsedTimeSec = elapsedTimeMs / 1000
                val elapsedTimeMin = elapsedTimeSec / 60
                val elapsedTimeHours = elapsedTimeMin / 60*/

                // Update the start time for next calculation
                startTime = currentTime

                Log.d(TimerActivity.TAG, "Elapsed time in hours: $elapsedTimeHours")

                // Now call updateUserGoalProgress with the calculated elapsed time
                updateUserGoalProgress(elapsedTimeHours.toDouble())

                // Now call updateTimesheetDuration and use the selected timesheet name from the spinner
                updateTimesheetDuration(timesheetName, elapsedTimeHours.toDouble())

                // Save the new start time and running state to SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putLong("startTime", startTime)
                editor.putBoolean("isTimerRunning", false)
                editor.apply()
            }
        }

        btnBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }











    private fun updateUserGoalProgress(elapsedTime: Double) {
        val userId = auth.currentUser?.uid
        val currentDate = getCurrentDate()

        if (userId!= null) {
            db.collection("goals").whereEqualTo("userId", userId).whereEqualTo("date", currentDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        Log.d(TimerActivity.TAG, "Document ID: ${document.id}")

                        // Attempt to retrieve the userGoalProgress as a string
                        val currentUserGoalProgressStr = document.getString("userGoalProgress")?: "0.0"

                        // Safely attempt to parse the string to a Double
                        val currentUserGoalProgress = try {
                            currentUserGoalProgressStr.toDouble()
                        } catch (e: NumberFormatException) {
                            // Handle the case where the string cannot be parsed to a Double
                            // For example, log an error or set a default value
                            Log.e(TimerActivity.TAG, "Failed to parse userGoalProgress: $currentUserGoalProgressStr")
                            0.0 // Default value
                        }

                        Log.d(TimerActivity.TAG, "Current User Goal Progress: $currentUserGoalProgress")
                        val updatedUserGoalProgress = currentUserGoalProgress + elapsedTime
                        Log.d(TimerActivity.TAG, "Updated User Goal Progress: $updatedUserGoalProgress")

                        document.reference.update("userGoalProgress", updatedUserGoalProgress.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "User Goal Progress updated successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TimerActivity.TAG, "Error updating User Goal Progress", e)
                            }
                    } else {
                        Log.w(TimerActivity.TAG, "No existing goal found for today.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TimerActivity.TAG, "Error fetching goal document", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }














    private fun updateTimesheetDuration(timesheetName:String,elapsedTime: Double) {
        val userId = auth.currentUser?.uid

        if (userId!= null) {
            db.collection("timesheet").whereEqualTo("userId", userId).whereEqualTo("timesheetName", timesheetName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        Log.d(TimerActivity.TAG, "Document ID: ${document.id}")
                        val selTaskDurationStr = document.getString("duration")?: "0.0"
                        val selTaskDuration = selTaskDurationStr.toDouble()
                        Log.d(TimerActivity.TAG, "Selected Task Duration: $selTaskDuration")
                        val updatedSelTaskDuration = selTaskDuration - elapsedTime
                        Log.d(TimerActivity.TAG, "Updated Selected Task Duration: $updatedSelTaskDuration")

                        document.reference.update("duration", updatedSelTaskDuration.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Selected Task Duration updated successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TimerActivity.TAG, "Error updating Selected Task Duration", e)
                            }
                    } else {
                        Log.w(TimerActivity.TAG, "No task found.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TimerActivity.TAG, "Error fetching timesheet document", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }










    fun getCurrentDate(): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return formatter.format(currentDate)
    }




}