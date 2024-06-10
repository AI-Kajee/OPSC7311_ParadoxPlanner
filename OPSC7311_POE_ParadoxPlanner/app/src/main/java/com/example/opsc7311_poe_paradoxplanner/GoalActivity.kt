package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var minGoalET: EditText
    private lateinit var maxGoalET: EditText
    private lateinit var saveGoalButton: Button
    private lateinit var backButton: Button

    companion object {
        private const val TAG = "Goal"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        minGoalET = findViewById(R.id.etMinGoals)
        maxGoalET = findViewById(R.id.etMaxGoals)
        saveGoalButton = findViewById(R.id.btnSaveGoals)
        backButton = findViewById(R.id.btnBack)

        saveGoalButton.setOnClickListener {
            Log.d(TAG, "Save Goals button clicked")

            val minGoal = minGoalET.text.toString().trim()
            val maxGoal = maxGoalET.text.toString().trim()

            // Your validation and saving code goes here...

        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
