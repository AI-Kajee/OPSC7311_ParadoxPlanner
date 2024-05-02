package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class TimeSheetListActivity : AppCompatActivity() {
    //userArrayList: ArrayList<User>
    private lateinit var timeSheetRecyclerView: RecyclerView
    private lateinit var entryArrayList: ArrayList<TimesheetEntry>
    private lateinit var timeSheetAdapter: TimeSheetAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnBack: Button

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_time_sheet_list)
//
//        timeSheetRecyclerView = findViewById(R.id.timeSheetRecyclerView)
//        timeSheetRecyclerView.layoutManager = LinearLayoutManager(this)
//        timeSheetRecyclerView.setHasFixedSize(true)
//
//        entryArrayList = arrayListOf()
//        timeSheetAdapter = TimeSheetAdapter(entryArrayList)
//        timeSheetRecyclerView.adapter = timeSheetAdapter
////        "2RHE1GUMzqdaVilL6uqipnFwXRa2"
//        val userId = auth.currentUser // Example user ID, replace with actual user ID logic
//        eventChangeListner(userId)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sheet_list)

        timeSheetRecyclerView = findViewById(R.id.timeSheetRecyclerView)
        timeSheetRecyclerView.layoutManager = LinearLayoutManager(this)
        timeSheetRecyclerView.setHasFixedSize(true)

        entryArrayList = arrayListOf()
        timeSheetAdapter = TimeSheetAdapter(entryArrayList)
        timeSheetRecyclerView.adapter = timeSheetAdapter
        btnBack = findViewById(R.id.btnBack)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            eventChangeListner(userId)
        } else {
            Log.e("User Error", "Current user is null")
        }



    }

    private fun eventChangeListner(userId: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("timesheets")
            .document(userId)  // Replace "userId" with the actual user ID
            .collection("entries")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            entryArrayList.add(dc.document.toObject(TimesheetEntry::class.java))
                        }
                    }
                    timeSheetAdapter.notifyDataSetChanged()
                }
            })

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}