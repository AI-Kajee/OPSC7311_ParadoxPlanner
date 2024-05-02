package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GoalTimer : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var spinnerProject: Spinner
    private lateinit var spinnerTask: Spinner
    private lateinit var timerContainer: LinearLayout
    private lateinit var max: EditText
    private lateinit var min: EditText

    private var isTimerRunning = false
    private var startTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_timer)

        chronometer = findViewById(R.id.chronometer)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        spinnerProject = findViewById(R.id.spinnerProject)
        spinnerTask = findViewById(R.id.spinnerTask)
        timerContainer = findViewById(R.id.timerContainer)
        min= findViewById( R.id.etMinGoals)
        max= findViewById(R.id.etMaxGoals)


        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        // Sample data for projects and tasks
      val projects = listOf("Project A")
        val tasks = listOf("Task 1")

      //  Set up adapters for spinners
        val projectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projects)
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProject.adapter = projectAdapter

        val taskAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tasks)
        taskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTask.adapter = taskAdapter

        // Fetch categories from Firestore and populate the spinner
        val userId = auth.currentUser?.uid
        if (userId!= null) {
            db.collection("categories").whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val categories = querySnapshot.documents.map { it.getString("categoryName")!! }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerProject.adapter = adapter
                }
                .addOnFailureListener { Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }

        val currentDate= getCurrentDate()
        val seletedCategory= spinnerProject.selectedItem.toString()
        val mingoals = min.text.toString().trim()
        val maxgoals = max.text.toString().trim()
        val gData= GoalData(
            currentDate,
            uid,
            maxgoals,
            mingoals,
            seletedCategory
        )

        db.collection("Goals")
            .add(gData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Added Goals", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Added Goals", Toast.LENGTH_SHORT).show()
            }

        // Event listener for when a project and task are selected
        spinnerProject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showTimerComponentsIfReady()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }

        spinnerTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showTimerComponentsIfReady()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }

        btnStart.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
            }
        }

        btnStop.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            }
        }
    }

    private fun showTimerComponentsIfReady() {
        val selectedProject = spinnerProject.selectedItem?.toString()
        val selectedTask = spinnerTask.selectedItem?.toString()

        if (selectedProject != null && selectedTask != null) {
            timerContainer.visibility = View.VISIBLE
        }
    }

    private fun startTimer() {
        startTimeInMillis = SystemClock.elapsedRealtime()
        chronometer.base = startTimeInMillis
        chronometer.start() // Start the timer
        isTimerRunning = true
    }

    private fun stopTimer() {
        val endTimeInMillis = SystemClock.elapsedRealtime()
        val elapsedMillis = endTimeInMillis - chronometer.base

        // Stop the timer
        chronometer.stop()
        isTimerRunning = false

        val elapsedSeconds = elapsedMillis / 1000
        val elapsedMinutes = elapsedSeconds / 60
        val elapsedHours = elapsedMinutes / 60

        // Display elapsed time to the user
        val formattedTime = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes % 60, elapsedSeconds % 60)
        Toast.makeText(this, "Elapsed time: $formattedTime", Toast.LENGTH_LONG).show()
    }
    fun getCurrentDate(): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        return formatter.format(currentDate)
    }

}