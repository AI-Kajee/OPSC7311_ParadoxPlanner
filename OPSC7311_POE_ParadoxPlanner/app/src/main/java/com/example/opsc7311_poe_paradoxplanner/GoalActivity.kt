package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GoalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var minGoalET : EditText
    private lateinit var maxGoalET : EditText
    private lateinit var saveGoalButton : Button

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

        saveGoalButton.setOnClickListener {
            Log.d(GoalActivity.TAG, "Save Goals button clicked")

            val minGoal = minGoalET.text.toString().trim()
            val maxGoal = maxGoalET.text.toString().trim()
            val currentDate = getCurrentDate()
            val userGoalProgress = 0

            // Check if goals are <= 24
            if(minGoal.toDouble() > 24 || maxGoal.toDouble() > 24){
                Log.d(GoalActivity.TAG, "Please ensure that your minimum goal and maximum goal is equal to or under 24 hours.")
                minGoalET.text.clear()
                maxGoalET.text.clear()
            }

            // Regular expression to match a valid number (integer or decimal)
            val numberPattern = "[+-]?[0-9]*\\.?[0-9]+([eE][+-]?[0-9]+)?".toRegex()

            if (minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                if (numberPattern.matches(minGoal) && numberPattern.matches(maxGoal)) {
                    val user = auth.currentUser
                    if (user!= null) {
                        val goalData = hashMapOf(
                            "userId" to user.uid,
                            "email" to user.email,
                            "minGoal" to minGoal,
                            "maxGoal" to maxGoal,
                            "date" to currentDate,
                            "userGoalProgress" to userGoalProgress.toString()
                        )

                        db.collection("goals").add(goalData)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Goals added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(GoalActivity.TAG, "Error adding goals", e)
                                Toast.makeText(this, "Failed to add goals. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid number for the minimum and maximum goal.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter the minimum and maximum goal.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return formatter.format(currentDate)
    }
}
