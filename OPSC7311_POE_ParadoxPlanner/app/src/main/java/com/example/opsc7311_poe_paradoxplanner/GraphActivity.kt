package com.example.opsc7311_poe_paradoxplanner

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "Graph"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        lineChart = findViewById(R.id.lineChart)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fetchGoalsData()
    }

    private fun fetchGoalsData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not logged in.")
            return
        }

        db.collection("goals")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val entries = ArrayList<Entry>()
                result.documents.forEach { document ->
                    val userGoalProgress = document.getString("userGoalProgress")?.toFloat() ?: 0f
                    val date = document.getString("date") ?: ""

                    val dateInMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)?.time ?: 0
                    entries.add(Entry(dateInMillis.toFloat(), userGoalProgress))
                }
                displayGraph(entries)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    private fun displayGraph(entries: ArrayList<Entry>) {
        val lineDataSet = LineDataSet(entries, "User Goal Progress")
        lineDataSet.color = Color.BLUE // Set your desired color here
        lineDataSet.setCircleColor(Color.BLACK) // Set your desired color here
        lineDataSet.lineWidth = 2f
        lineDataSet.valueTextSize = 10f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Formatting X axis to display date
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return dateFormat.format(Date(value.toLong()))
            }
        }

        lineChart.invalidate() // Refresh the chart
    }
}
