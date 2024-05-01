package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_list)

        categoryListRecyclerView = findViewById(R.id.categoryListRecyclerView)
        categoryListRecyclerView.layoutManager=LinearLayoutManager(this)
        categoryListRecyclerView.setHasFixedSize(true)

        categoryDC = arrayListOf()

        categoryListAdapter = CategoryListAdapter(categoryDC)

        categoryListRecyclerView.adapter = categoryListAdapter

        EventChangeListener()



    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("categories")
            .addSnapshotListener { value, error ->
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

}
