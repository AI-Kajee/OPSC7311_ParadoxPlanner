package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GoalActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var minGoalET: EditText
    private lateinit var maxGoalET: EditText
    private lateinit var saveGoalButton: Button
    private lateinit var backButton: Button
    private lateinit var lineChart: LineChart

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
        lineChart = findViewById(R.id.lineChart)

        saveGoalButton.setOnClickListener {
            Log.d(TAG, "Save Goals button clicked")

            val minGoal = minGoalET.text.toString().trim()
            val maxGoal = maxGoalET.text.toString().trim()
            val currentDate = getCurrentDate()
            val userGoalProgress = 0.0

            val numberPattern = "[+-]?[0-9]*\\.?[0-9]+([eE][+-]?[0-9]+)?".toRegex()

            if (minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                if (minGoal.toDouble() > 24 || maxGoal.toDouble() > 24) {
                    Log.d(TAG, "Please ensure that your minimum goal and maximum goal is equal to or under 24 hours.")
                    minGoalET.text.clear()
                    maxGoalET.text.clear()
                } else {
                    if (numberPattern.matches(minGoal) && numberPattern.matches(maxGoal)) {
                        val user = auth.currentUser
                        if (user != null) {
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
                                    fetchGoalsData()  // Refresh the graph data after adding new goals
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding goals", e)
                                }
                        } else {
                            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid number for the minimum and maximum goal.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter the minimum and maximum goal.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Initially load the graph data
        fetchGoalsData()
    }

    private fun fetchGoalsData() {
        db.collection("goals")
            .get()
            .addOnSuccessListener { result ->
                val entries = ArrayList<Entry>()
                var maxGoal = 0f
                var minGoal = 0f
                result.documents.forEach { document ->
                    val max = document.getString("maxGoal")?.toFloat() ?: 0f
                    val min = document.getString("minGoal")?.toFloat() ?: 0f
                    val userGoalProgress = document.getString("userGoalProgress")?.toFloat() ?: 0f
                    val date = document.getString("date") ?: ""

                    val dateInMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)?.time ?: 0
                    entries.add(Entry(dateInMillis.toFloat(), userGoalProgress))

                    if (max > maxGoal) maxGoal = max
                    if (minGoal == 0f || min < minGoal) minGoal = min
                }
                displayGraph(entries, maxGoal, minGoal)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun displayGraph(entries: ArrayList<Entry>, maxGoal: Float, minGoal: Float) {
        val lineDataSet = LineDataSet(entries, "Hours Worked")
        lineDataSet.color = Color.BLUE
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.lineWidth = 2f
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 12f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.axisLeft.axisMinimum = minGoal
        lineChart.axisLeft.axisMaximum = maxGoal
        lineChart.axisRight.isEnabled = false

        // Formatting X axis to display date
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(Date(value.toLong()))
            }
        }

        lineChart.invalidate() // Refresh the chart
    }

    private fun getCurrentDate(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return current.format(formatter)
    }
}
