package com.example.opsc7311_poe_paradoxplanner

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
            Log.d(TAG, "Save Goals button clicked")

            val minGoal = minGoalET.text.toString().trim()
            val maxGoal = maxGoalET.text.toString().trim()


            if(minGoal==null || maxGoal==null){
                Toast.makeText(this, "Please enter a minimum and maximum goal.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal == null || maxGoal == null) {
                Toast.makeText(this, "Invalid input. Please enter numbers only.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (maxGoal.toDouble() > 24) {
                Toast.makeText(this, "Maximum goal cannot be greater than 24.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal.toDouble() > 24) {
                Toast.makeText(this, "Minimum goal cannot be greater than 24.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (minGoal >= maxGoal) {
                Toast.makeText(this, "Min goal cannot be > or = to maximum goal.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



        }

    }
}
