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
                Log.d(TAG, "Firebase documents retrieved: ${result.documents.size}")
                result.documents.forEachIndexed { index, document ->
                    try {
                        val userGoalProgress = document.getString("userGoalProgress")?.toFloat() ?: 0f
                        val date = document.getString("date") ?: ""
                        Log.d(TAG, "Document ${document.id}: userGoalProgress=$userGoalProgress, date=$date")

                        val dateInMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date)?.time ?: 0
                        entries.add(Entry(dateInMillis.toFloat(), userGoalProgress))
                    } catch (e: Exception) {
                        Log.e(TAG, "Skipping document with incorrect format: ${document.id}", e)
                    }
                }

                Log.d(TAG, "Entries size after fetching data: ${entries.size}")

                runOnUiThread {
                    // Ensure entries are sorted by x-axis value (date in this case)
                    entries.sortBy { it.x }

                    val lineDataSet = LineDataSet(entries, "User Goal Progress")
                    lineDataSet.color = Color.BLUE // Set your desired color here
                    lineDataSet.setCircleColor(Color.BLACK) // Set your desired color here
                    lineDataSet.lineWidth = 2f
                    lineDataSet.valueTextSize = 10f

                    // Set the mode to CUBIC_BEZIER or LINEAR to ensure the points are connected
                    lineDataSet.mode = LineDataSet.Mode.LINEAR

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

                    lineChart.xAxis.granularity = 1f // Ensure the axis labels are displayed properly
                    lineChart.invalidate() // Refresh the chart
                    Log.d(TAG, "Graph updated with ${entries.size} entries")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }
}
