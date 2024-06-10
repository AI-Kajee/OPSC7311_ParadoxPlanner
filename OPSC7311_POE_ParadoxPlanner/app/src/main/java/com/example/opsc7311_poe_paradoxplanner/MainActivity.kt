package com.example.opsc7311_poe_paradoxplanner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnGoToCategoryActivity = findViewById<Button>(R.id.btnGoToCategoryActivity)
        val btnGoToTimesheetActivity = findViewById<Button>(R.id.btnGoToTimesheetActivity)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)
        val btnTimeSheetList = findViewById<Button>(R.id.btnTimeSheetList)
        val btnCategoryList= findViewById<Button>(R.id.btnCategoryList)
        val btnGoalTimer = findViewById<Button>(R.id.btnGoalTimer)
        val btnTimer = findViewById<Button>(R.id.btnTimer)
        val btnGoToPieChartActivity = findViewById<Button>(R.id.btnGoToPieChartActivity)
        val btnNoteScreen = findViewById<Button>(R.id.btnNoteScreen)
        val btnGraph = findViewById<Button>(R.id.btnGraph)

        btnGoToCategoryActivity.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        btnGraph.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        btnNoteScreen.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        btnGoToTimesheetActivity.setOnClickListener {
            val intent = Intent(this, TimesheetActivity::class.java)
            startActivity(intent)
        }
        btnGoalTimer.setOnClickListener {
            val intent = Intent(this, GoalActivity::class.java)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener {
            auth.signOut() // Sign out the user
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            // Optionally, navigate the user back to the login screen
            val intent = Intent(this, LoginActivity::class.java) // Replace LoginActivity with your actual login activity
            startActivity(intent)
            finish() // Close the current activity
        }

        btnTimeSheetList.setOnClickListener {
            val intent = Intent(this, TimeSheetListActivity::class.java)
            startActivity(intent)
        }

        btnCategoryList.setOnClickListener {
            val intent = Intent(this, CategoryListActivity::class.java)
            startActivity(intent)
        }

        btnTimer.setOnClickListener{
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        btnGoToPieChartActivity.setOnClickListener {
            val intent = Intent(this, PieChart::class.java)
            startActivity(intent)
        }


    }
}
