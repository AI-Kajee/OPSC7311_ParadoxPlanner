package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryListActivity : AppCompatActivity() {
    private lateinit var categoryListRecyclerView: RecyclerView
    private lateinit var categoryDC: ArrayList<CategoryDC>
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var btnBackm: Button
    private lateinit var categorySeekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        categoryListRecyclerView = findViewById(R.id.categoryListRecyclerView)
        categoryListRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryListRecyclerView.setHasFixedSize(true)
        btnBackm = findViewById(R.id.btnBackm)
        categorySeekBar = findViewById(R.id.categorySeekBar)

        categoryDC = arrayListOf()

        categoryListAdapter = CategoryListAdapter(categoryDC, 100.0) // Initialize with 0.0 max hours

        categoryListRecyclerView.adapter = categoryListAdapter

        EventChangeListener()

        btnBackm.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up SeekBar listener
        categorySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Convert the SeekBar's progress to a Double representing the total hours
                val targetTotalHours = progress.toDouble()
                // Display the current SeekBar value
                Toast.makeText(this@CategoryListActivity, "Current SeekBar Value: $targetTotalHours", Toast.LENGTH_SHORT).show()
                // Fetch categories from Firestore where totalHours equals the SeekBar's value
                fetchCategories(targetTotalHours)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle when the user starts moving the SeekBar
                Toast.makeText(this@CategoryListActivity, "Started moving SeekBar", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle when the user stops moving the SeekBar
                Toast.makeText(this@CategoryListActivity, "Stopped moving SeekBar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCategories(targetTotalHours: Double) {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser!= null) {

            if(targetTotalHours==100.0){
                val query2 = db.collection("categories")
                    .whereEqualTo("userId", currentUser.uid) // Filter categories by the current user's ID


                query2.addSnapshotListener { value, error ->
                    if (error!= null) {
                        // Handle error
                        return@addSnapshotListener
                    }

                    value?.let { snapshot ->
                        categoryDC.clear()
                        for (document in snapshot.documents) {
                            val category = document.toObject(CategoryDC::class.java)
                            category?.let { categoryDC.add(it) }
                        }
                        categoryListAdapter.notifyDataSetChanged()
                    }
                }
            }else if(targetTotalHours<100.0) {
                val query = db.collection("categories")
                    .whereEqualTo(
                        "userId",
                        currentUser.uid
                    ) // Filter categories by the current user's ID
                    .whereEqualTo(
                        "totalHours",
                        targetTotalHours
                    ) // Filter categories where totalHours equals the SeekBar's value

                query.addSnapshotListener { value, error ->
                    if (error != null) {
                        // Handle error
                        return@addSnapshotListener
                    }

                    value?.let { snapshot ->
                        categoryDC.clear()
                        for (document in snapshot.documents) {
                            val category = document.toObject(CategoryDC::class.java)
                            category?.let { categoryDC.add(it) }
                        }
                        categoryListAdapter.notifyDataSetChanged()
                    }
                }
            }
        } else {
            // Handle case where no user is logged in
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun EventChangeListener() {
        // Your existing EventChangeListener code
    }
}
