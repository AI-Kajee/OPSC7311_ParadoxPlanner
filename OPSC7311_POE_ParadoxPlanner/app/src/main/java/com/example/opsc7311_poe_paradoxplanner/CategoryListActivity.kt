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
import com.google.firebase.firestore.EventListener
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

        categoryListAdapter = CategoryListAdapter(categoryDC, 0.0) // Initialize with 0.0 max hours

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
                val maxHours = progress.toDouble() // Adjust this multiplier based on your needs
                categoryListAdapter.maxHours = maxHours
                categoryListAdapter.notifyDataSetChanged() // Notify the adapter of the changes
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

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser!= null) {
            db.collection("categories")
                .whereEqualTo("userId", currentUser.uid) // Filter categories by the current user's ID
                .addSnapshotListener { value, error ->
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
        } else {
            // Handle case where no user is logged in
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
