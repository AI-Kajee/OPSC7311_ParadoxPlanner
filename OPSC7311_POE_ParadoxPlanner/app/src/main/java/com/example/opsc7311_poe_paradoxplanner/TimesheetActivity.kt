package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import android.widget.AbsSpinner
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TimesheetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var timesheetNameEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var descriptionNameEditText: EditText
    private lateinit var photoUrlEditText: EditText
    private lateinit var btnUploadProfilePicture: Button
    private lateinit var btnSave: Button

    companion object {
        private const val TAG = "TimesheetActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timesheet)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnUploadProfilePicture = findViewById(R.id.editTextCategoryName)
        btnSave = findViewById(R.id.btnCreateCategory)
        timesheetNameEditText = findViewById(R.id.editTextCategoryName)
        startTimeEditText = findViewById(R.id.editTextCategoryName)
        endTimeEditText = findViewById(R.id.editTextCategoryName)
        startDateEditText = findViewById(R.id.editTextCategoryName)
        endDateEditText = findViewById(R.id.editTextCategoryName)
        categorySpinner = findViewById(R.id.editTextCategoryName)
        descriptionNameEditText = findViewById(R.id.editTextCategoryName)
        photoUrlEditText = findViewById(R.id.editTextCategoryName)




        // Fetch user profile information from Firestore
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val categoryName = document.getString("categoryName")

                        // Populate the UI elements with the retrieved data
                        categoryNameEditText.setText(categoryName)

                    } else {
                        Log.d(CategoryActivity.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(CategoryActivity.TAG, "get failed with ", exception)
                }
        }


    }
}