package com.example.opsc7311_paradoxplanner_poe.ui.category

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel:ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createCategory(categoryName: String) {
        val userId = auth.currentUser?.uid ?: return
        val category = hashMapOf(
            "name" to categoryName,
            "userId" to userId // Associate the category with the user
        )

        db.collection("categories")
            .add(category)
            .addOnSuccessListener { documentReference ->
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }

}
