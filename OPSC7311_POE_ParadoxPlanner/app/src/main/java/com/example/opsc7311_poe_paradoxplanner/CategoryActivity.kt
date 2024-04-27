package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var createCategoryButton: Button
    private lateinit var categoryNameEditText: EditText
    private lateinit var btnBack: Button // Declaration of btnBack

    companion object {
        private const val TAG = "CategoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        createCategoryButton = findViewById(R.id.btnCreateCategory)
        categoryNameEditText = findViewById(R.id.editTextCategoryName)
        btnBack = findViewById(R.id.btnBack) // Initialization of btnBack

        createCategoryButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()
            if (categoryName.isNotEmpty()) {
                // Get the current user's ID
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    // Create a new category document in the 'categories' collection, associated with the user
                    db.collection("categories").add(hashMapOf("name" to categoryName, "userId" to userId))
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Category created: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error creating category", e)
                        }
                } else {
                    Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a category name.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
