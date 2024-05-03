package com.example.opsc7311_poe_paradoxplanner

import android.content.ContentValues.TAG
import android.content.Intent
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
    private  lateinit var back: Button

    private var isTimerRunning = false
    private var startTimeInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_timer)

//        chronometer = findViewById(R.id.chronometer)
//        btnStart = findViewById(R.id.btnStart)
//        btnStop = findViewById(R.id.btnStop)
        spinnerProject = findViewById(R.id.spinnerProject)
        back = findViewById(R.id.btnBack)

//        timerContainer = findViewById(R.id.timerContainer)
        min= findViewById( R.id.etMinGoals)
        max= findViewById(R.id.etMaxGoals)


        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        // Sample data for projects and tasks
      val projects = listOf(" ")


      //  Set up adapters for spinners
        val projectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projects)
        projectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProject.adapter = projectAdapter



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
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }

        val currentDate= getCurrentDate()
        val seletedCategory= spinnerProject.selectedItem.toString()
        val mingoals = min.toString()
        val maxgoals = max.toString()

// Check if they're not empty before storing in Firestore
        if (mingoals.isEmpty() || maxgoals.isEmpty()) {
            Toast.makeText(this, "Please enter both minimum and maximum goals.", Toast.LENGTH_SHORT).show()
            return
        }
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

    }

    fun getCurrentDate(): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        return formatter.format(currentDate)
    }

}