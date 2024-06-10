package com.example.opsc7311_poe_paradoxplanner

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

class NoteActivity : AppCompatActivity() {

    private lateinit var btnAddNote: Button
    private lateinit var btnViewNote: Button
    private lateinit var btnBack: Button
    private lateinit var lstNotes: ListView
    private lateinit var tvSelectDate: TextView
    private lateinit var tvTaskName: TextView
    private lateinit var tvTaskDesc: TextView
    private lateinit var tvSelectDateToView: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    companion object {
        private const val TAG = "NoteActivity"
    }

    private var selectedDate: Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnAddNote = findViewById(R.id.btnAddNote)
        btnViewNote = findViewById(R.id.btnViewNote)
        btnBack = findViewById(R.id.btnBack)
        lstNotes = findViewById(R.id.lstNotes)
        tvSelectDate = findViewById(R.id.tvSelectDate)
        tvTaskName = findViewById(R.id.tvTaskName)
        tvTaskDesc = findViewById(R.id.tvTaskDesc)
        tvSelectDateToView = findViewById(R.id.tvSelectDateToView)

        initializeDatePickers()

        btnAddNote.setOnClickListener {
            Log.d(NoteActivity.TAG, "Add Note button clicked")

            val noteName = tvTaskName.text.toString()
            val noteDescripton = tvTaskDesc.text.toString()
            val selectedDate = tvSelectDate.text.toString()

            if (noteName.isNotEmpty()) {
                val user = auth.currentUser
                if (user!= null) {
                    val noteData = hashMapOf(
                        "userId" to user.uid,
                        "email" to user.email,
                        "selectedDate" to selectedDate,
                        "noteName" to noteName,
                        "noteDescription" to noteDescripton
                    )

                    db.collection("notes").add(noteData)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                this,
                                "Note added: ${documentReference.id}",
                                Toast.LENGTH_SHORT
                            ).show()
                            fetchAllNotes() // Refresh the ListView after adding a note
                        }
                        .addOnFailureListener { e ->
                            Log.w(NoteActivity.TAG, "Error adding note", e)
                        }
                } else {
                    Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a note name.", Toast.LENGTH_SHORT).show()
            }


        }

        btnViewNote.setOnClickListener {
            Log.d(TAG, "View Note button clicked")
            val selectedDateToView = tvSelectDateToView.text.toString()
            fetchAndDisplayNote(selectedDateToView)
        }



        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }




        fetchAllNotes() // Populate the ListView initially

    }














    private fun initializeDatePickers() {
        tvSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    tvSelectDate.setText(
                        "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH) + 1}/${
                            calendar.get(
                                Calendar.DAY_OF_MONTH
                            )
                        }"
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        tvSelectDateToView.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    calendar.set(year, monthOfYear, dayOfMonth)
                    tvSelectDateToView.setText(
                        "${calendar.get(Calendar.YEAR)}/${
                            calendar.get(
                                Calendar.MONTH
                            ) + 1
                        }/${calendar.get(Calendar.DAY_OF_MONTH)}"
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }














    private fun fetchAndDisplayNote(selectedDateString: String) {
        val user = auth.currentUser
        if (user!= null) {
            db.collection("notes").whereEqualTo("userId", user.uid).whereEqualTo("selectedDate", selectedDateString)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val notesList = mutableListOf<String>()
                        for (document in documents) {
                            val noteDescription = document.getString("noteDescription")
                            val noteName = document.getString("noteName")
                            val selectedDate = document.getString("selectedDate")

                            // Construct a single string to represent each note
                            val noteInfo = "$noteName - $selectedDate\n$noteDescription"
                            notesList.add(noteInfo)
                        }
                        // Displaying multiple notes in a dialog
                        AlertDialog.Builder(this)
                            .setTitle("Notes for $selectedDateString")
                            .setMultiChoiceItems(notesList.toTypedArray(), null) { _, which, isChecked ->
                                if (isChecked) {
                                    deleteNote(selectedDateString)

                                } else {

                                }
                            }
                            .setPositiveButton(android.R.string.ok) { _, _ ->

                            }
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show()
                    } else {
                        Toast.makeText(this, "No notes found for the selected date.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun fetchAllNotes() {
        val user = auth.currentUser
        if (user!= null) {
            db.collection("notes").whereEqualTo("userId", user.uid).get()
                .addOnSuccessListener { documents ->
                    val notesList = mutableListOf<String>()
                    for (document in documents) {
                        val noteDescription = document.getString("noteDescription")
                        val noteName = document.getString("noteName")
                        val selectedDate = document.getString("selectedDate")

                        // Construct a single string to represent each note
                        val noteInfo = "$selectedDate: $noteName"
                        notesList.add(noteInfo)
                    }
                    lstNotes.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notesList)
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting documents: $exception", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }





    private fun deleteNote(selectedDate: String) {
        val userId = auth.currentUser?.uid

        if (userId!= null) {
            db.collection("notes").whereEqualTo("userId", userId).whereEqualTo("selectedDate", selectedDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.first()
                        Log.d(NoteActivity.TAG, "Document ID: ${document.id}")

                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Note deleted successfully.", Toast.LENGTH_SHORT).show()
                                fetchAllNotes()
                            }
                            .addOnFailureListener { e ->
                                Log.e(NoteActivity.TAG, "Error deleting note.", e)
                            }
                    } else {
                        Log.w(NoteActivity.TAG, "No note found.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(NoteActivity.TAG, "Error fetching note document", exception)
                }
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }











}