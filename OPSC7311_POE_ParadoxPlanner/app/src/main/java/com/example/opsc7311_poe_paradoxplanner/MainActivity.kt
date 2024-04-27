package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnGoToCategoryActivity = findViewById<Button>(R.id.btnGoToCategoryActivity)
        val btnGoToTimesheetActivity = findViewById<Button>(R.id.btnGoToTimesheetActivity)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)

        btnGoToCategoryActivity.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        btnGoToTimesheetActivity.setOnClickListener {
            val intent = Intent(this, TimesheetActivity::class.java)
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

    }
}
