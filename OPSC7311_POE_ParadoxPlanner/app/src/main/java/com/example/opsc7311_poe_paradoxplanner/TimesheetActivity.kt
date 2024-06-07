package com.example.opsc7311_poe_paradoxplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TimesheetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var timesheetNameEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var startDateEditText: EditText
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
            Log.d(TimesheetActivity.TAG, "Save button clicked")

            val timesheetName = timesheetNameEditText.text.toString()
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            val startDate = startDateEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()
            val duration = calculateDurationHours().toDouble()

            if (timesheetName.isNotEmpty()) {
                val user = auth.currentUser
                if (user!= null) {
                    val timesheetData = mutableMapOf<String, Any>(
                        "userId" to user.uid,
                        "timesheetName" to timesheetName,
                        "startTime" to startTime,
                        "endTime" to endTime,
                        "startDate" to startDate,
                        "duration" to duration.toString(),
                        "category" to category,
                        "description" to description
                    )

                    if (selectedImageUri!= null) {
                        // Convert image to Base64
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri!!)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream)
                        val bytes = byteArrayOutputStream.toByteArray()
                        val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                        // Store the Base64 string in timesheetData
                        timesheetData["image"] = base64Image

                        // Add timesheet data to Firestore
                        db.collection("timesheet").add(timesheetData)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Timesheet entry added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                                // After successful addition, update the category total hours
                                updateCategoryTotalHours(category, duration.toDouble())
                            }
                            .addOnFailureListener { e ->
                                Log.w(TimesheetActivity.TAG, "Error adding timesheet entry", e)
                            }
                    } else {
                        // No image selected, just add the timesheet data to Firestore
                        db.collection("timesheet").add(timesheetData)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Timesheet entry added: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                                // After successful addition, update the category total hours
                                updateCategoryTotalHours(category, duration)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TimesheetActivity.TAG, "Error adding timesheet entry", e)
                            }
                    }
                } else {
                    Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a timesheet entry name.", Toast.LENGTH_SHORT).show()
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
    }




    private fun initializeTimePickers() {
        startTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                // Ensure the time is displayed in 24-hour format
                startTimeEditText.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
        endTimeEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                // Ensure the time is displayed in 24-hour format
                endTimeEditText.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

    }











    private fun calculateDurationMinutes(): Long {
        val startTimeText = startTimeEditText.text.toString()
        val endTimeText = endTimeEditText.text.toString()

        // Check if both start and end times are not empty
        if (startTimeText.isNotBlank() && endTimeText.isNotBlank()) {
            val startTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(startTimeText)
            val endTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(endTimeText)

            val diffInMillies = TimeUnit.MILLISECONDS.toMillis(endTime.time - startTime.time)
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillies)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies) % 60
            val totalDurationInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillies)
            
            val adjustedDurationInSeconds = totalDurationInSeconds

            // Convert back to hours and minutes if needed
            val adjustedHours = TimeUnit.SECONDS.toHours(adjustedDurationInSeconds)
            val adjustedMinutes = TimeUnit.SECONDS.toMinutes(adjustedDurationInSeconds) % 60

            return TimeUnit.HOURS.toMinutes(adjustedHours) + adjustedMinutes
        } else {
            // Handle case where either start or end time is missing
            Toast.makeText(this, "Please enter both start and end times.", Toast.LENGTH_SHORT).show()
            return 0L
        }
    }









    private fun calculateDurationHours(): Long {
        val startTimeText = startTimeEditText.text.toString()
        val endTimeText = endTimeEditText.text.toString()

        // Check if both start and end times are not empty
        if (startTimeText.isNotBlank() && endTimeText.isNotBlank()) {
            val startTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(startTimeText)
            val endTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(endTimeText)

            val diffInMillies = TimeUnit.MILLISECONDS.toMillis(endTime.time - startTime.time)
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillies)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillies) % 60

            // Calculate total duration in hours
            val totalDurationInHours = TimeUnit.MILLISECONDS.toHours(diffInMillies)

            return totalDurationInHours
        } else {
            // Handle case where either start or end time is missing
            Toast.makeText(this, "Please enter both start and end times.", Toast.LENGTH_SHORT).show()
            return 0L
        }
    }







    private fun updateCategoryTotalHours(categoryName: String, totalHours: Double) {
        val userId = auth.currentUser?.uid
        if (userId!= null) {
            db.collection("categories").whereEqualTo("userId", userId).whereEqualTo("categoryName", categoryName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        val currentTotalHours = document.getDouble("totalHours")?: 0.0
                        val updatedTotalHours = currentTotalHours + totalHours

                        document.reference.update("totalHours", updatedTotalHours)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Category total hours updated successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error updating category total hours", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching category document", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }




}
