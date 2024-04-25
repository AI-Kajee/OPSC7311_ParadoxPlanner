package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class CategoryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var createCategoryButton: Button
    private lateinit var categoryNameEditText: EditText

    companion object {
        private const val TAG = "CategoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        createCategoryButton = findViewById(R.id.btnCreateCategory)
        categoryNameEditText = findViewById(R.id.editTextCategoryName)


        // Fetch user profile information from Firestore
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val categoryName = document.getString("categoryName")

                        // Populate the UI elements with the retrieved data
                        categoryNameEditText.setText(categoryName)

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }


        createCategoryButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()


            // Update Firestore with new information
            auth.currentUser?.let { user ->
                // Create a Student object with the updated information
                val updatedCategory = Category(
                    userId = user.uid,
                    categoryName=categoryName
                )

                // Convert the Student object to a map and update Firestore
                val userCategory = hashMapOf(
                    "userId" to updatedCategory.userId,
                    "categoryName" to updatedCategory.username,
                )
                db.collection("categories").document(user.uid).set(userCategory, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            }
        }



    }
}