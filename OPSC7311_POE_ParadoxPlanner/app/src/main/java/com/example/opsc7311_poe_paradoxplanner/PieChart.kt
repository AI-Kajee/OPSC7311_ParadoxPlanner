package com.example.opsc7311_poe_paradoxplanner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        findViewById<Button>(R.id.buttonColors1).setOnClickListener {
            updatePieChartColors(listOf(Color.RED, Color.BLUE))
        }
        findViewById<Button>(R.id.buttonColors2).setOnClickListener {
            updatePieChartColors(listOf(Color.GREEN, Color.YELLOW))
        }
        findViewById<Button>(R.id.buttonColors3).setOnClickListener {
            updatePieChartColors(listOf(Color.CYAN, Color.MAGENTA))
        }

        loadData()
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
        pieChart.invalidate() // refresh the chart
    }

    private fun updatePieChartColors(colors: List<Int>) {
        val dataSet = pieChart.data?.dataSet as? PieDataSet ?: return
        dataSet.colors = colors
        pieChart.invalidate() // refresh the chart
    }
}
