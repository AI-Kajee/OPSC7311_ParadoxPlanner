package com.example.opsc7311_poe_paradoxplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

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
    private lateinit var uploadPictureButton: Button
    private lateinit var saveButton: Button
    private lateinit var pictureImageView: ImageView
    private lateinit var btnBack: Button

    companion object {
        private const val TAG = "TimesheetActivity"
        private const val PICK_IMAGE_REQUEST = 100
    }

    private var selectedImageUri: Uri? = null

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
        pictureImageView = findViewById(R.id.pictureImageView)
        btnBack = findViewById(R.id.btnBack)

        // Initialize date and time picker listeners
        initializeDatePickers()
        initializeTimePickers()

        // Fetch categories from Firestore and populate the spinner
        val userId = auth.currentUser?.uid
        if (userId!= null) {
            db.collection("categories").whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val categories = querySnapshot.documents.map { it.getString("categoryName")!! }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error fetching categories: ", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }

        // Handling button click for uploading profile picture
        uploadPictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
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

            if (selectedImageUri!= null) { // Check if an image was selected
                val imageRef = FirebaseStorage.getInstance().getReference("images/${auth.currentUser?.uid}/${System.currentTimeMillis()}.jpg")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        // Get download URL after upload
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val timesheetEntry = hashMapOf(
                                "timesheetName" to timesheetName,
                                "startTime" to startTime,
                                "endTime" to endTime,
                                "startDate" to startDate,
                                "endDate" to endDate,
                                "category" to category,
                                "description" to description,
                                "imageUrl" to uri.toString()
                            )

                            // Save the timesheet entry to Firestore
                            auth.currentUser?.let { user ->
                                db.collection("timesheets").document(user.uid).collection("entries")
                                    .add(timesheetEntry)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "Timesheet entry saved with ID: ${documentReference.id}")
                                        Toast.makeText(this, "Timesheet entry has been successfully created.", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "Error saving timesheet entry: ", exception)
                                        Toast.makeText(this, "Failed to create timesheet entry.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.data!= null) {
            selectedImageUri = data.data // Store the URI of the selected image
            pictureImageView.setImageURI(selectedImageUri) // Display the selected image
        }
    }

    private fun initializeDatePickers() {
        startDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                startDateEditText.setText("${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        endDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                endDateEditText.setText("${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)}")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun initializeTimePickers() {
        startTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                startTimeEditText.setText("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
        endTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                endTimeEditText.setText("${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }
}
