package com.example.opsc7311_poe_paradoxplanner

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PieChart : AppCompatActivity() {
    private lateinit var pieChart: PieChart
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var buttonColors1: Button
    private lateinit var buttonColors2: Button
    private lateinit var buttonColors3: Button
    private lateinit var badgeCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pie_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pieChart = findViewById(R.id.pieChart)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        buttonColors1 = findViewById(R.id.buttonColors1)
        buttonColors2 = findViewById(R.id.buttonColors2)
        buttonColors3 = findViewById(R.id.buttonColors3)
        badgeCountTextView = findViewById(R.id.badgeCount)

        buttonColors1.setOnClickListener {
            updatePieChartColors(listOf(Color.RED, Color.BLUE))
        }
        buttonColors2.setOnClickListener {
            updatePieChartColors(listOf(Color.GREEN, Color.YELLOW))
        }
        buttonColors3.setOnClickListener {
            updatePieChartColors(listOf(Color.CYAN, Color.MAGENTA))
        }

        loadData()
    }

    @SuppressLint("SetTextI18n")
    private fun updateButtons(badgeCount: Int) {
        badgeCountTextView.text = "Number of badges: $badgeCount"
        if (badgeCount >= 1) {
            buttonColors1.isEnabled = true
            buttonColors1.isVisible = true
        }
        if (badgeCount >= 3) {
            buttonColors2.isEnabled = true
            buttonColors2.isVisible = true
        }
        if (badgeCount >= 5) {
            buttonColors3.isEnabled = true
            buttonColors3.isVisible = true
        }
    }

    private fun loadData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("goals")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                var completedGoals = 0
                var uncompletedGoals = 0

                for (document in documents) {
                    val userGoalProgress = document.getString("userGoalProgress")?.toDoubleOrNull() ?: 0.0
                    val minGoal = document.getString("minGoal")?.toDoubleOrNull() ?: 0.0

                    if (userGoalProgress > minGoal) {
                        completedGoals++
                    } else {
                        uncompletedGoals++
                    }
                }

                setupPieChart(completedGoals, uncompletedGoals, listOf(Color.GRAY, Color.DKGRAY))
                updateButtons(completedGoals)
            }
            .addOnFailureListener { exception ->
                Log.w("PieChartActivity", "Error getting documents: ", exception)
            }
    }

    private fun setupPieChart(completedGoals: Int, uncompletedGoals: Int, colors: List<Int>) {
        val entries = listOf(
            PieEntry(completedGoals.toFloat(), "Completed Goals"),
            PieEntry(uncompletedGoals.toFloat(), "Uncompleted Goals")
        )

        val dataSet = PieDataSet(entries, "Goals")
        dataSet.colors = colors
        dataSet.valueTextSize = 18f

        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false // Disable the description
        pieChart.legend.isEnabled = false // Disable the legend (color key)
        pieChart.invalidate() // refresh the chart
    }

    private fun updatePieChartColors(colors: List<Int>) {
        val dataSet = pieChart.data?.dataSet as? PieDataSet ?: return
        dataSet.colors = colors
        pieChart.invalidate() // refresh the chart
    }
}
