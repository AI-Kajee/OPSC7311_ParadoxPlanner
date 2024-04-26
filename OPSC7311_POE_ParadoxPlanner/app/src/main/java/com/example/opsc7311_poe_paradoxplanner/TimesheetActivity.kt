package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private lateinit var descriptionEditText: EditText
    private lateinit var photoUrlEditText: EditText
    private lateinit var uploadPictureButton: Button
    private lateinit var saveButton: Button
    private lateinit var pictureImageView: ImageView

    companion object {
        private const val TAG = "TimesheetActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        uploadPictureButton = findViewById(R.id.btnUploadPicture)
        saveButton = findViewById(R.id.btnSave)
        timesheetNameEditText = findViewById(R.id.timesheetNameEditText)
        startTimeEditText = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)
        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        photoUrlEditText = findViewById(R.id.photoUrlEditText)
        pictureImageView = findViewById(R.id.picture)

        // Fetch categories from Firestore and populate the spinner
        db.collection("categories").get()
            .addOnSuccessListener { querySnapshot ->
                val categories = querySnapshot.documents.map { it.getString("name")!! }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error fetching categories: ", exception)
            }

        // Handling button click for uploading profile picture
        uploadPictureButton.setOnClickListener {
            val url = photoUrlEditText.text.toString()
            if (url.isNotEmpty()) {
                // Use Glide to load the image from the URL with placeholders and error handling
                Glide.with(this).load(url).into(pictureImageView)
                Toast.makeText(this, "Picture updated.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid URL.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handling button click for saving timesheet entry
        saveButton.setOnClickListener {
            val timesheetName = timesheetNameEditText.text.toString()
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()
            val photoUrl = photoUrlEditText.text.toString()

            if (timesheetName.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && category.isNotEmpty() && description.isNotEmpty()) {
                // Create a map with the timesheet entry data
                val timesheetEntry = hashMapOf(
                    "timesheetName" to timesheetName,
                    "startTime" to startTime,
                    "endTime" to endTime,
                    "startDate" to startDate,
                    "endDate" to endDate,
                    "category" to category,
                    "description" to description,
                    "photoUrl" to photoUrl
                )

                // Save the timesheet entry to Firestore
                auth.currentUser?.let { user ->
                    db.collection("timesheets").document(user.uid).collection("entries").add(timesheetEntry)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "Timesheet entry saved with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error saving timesheet entry: ", exception)
                        }
                }
            } else {
                Toast.makeText(this, "All fields must be filled.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
