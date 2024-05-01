package com.example.opsc7311_poe_paradoxplanner

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sheet_list)

        timeSheetRecyclerView = findViewById(R.id.timeSheetRecyclerView)
        timeSheetRecyclerView.layoutManager = LinearLayoutManager(this)
        timeSheetRecyclerView.setHasFixedSize(true)

        entryArrayList = arrayListOf()

        timeSheetAdapter = TimeSheetAdapter(entryArrayList)

        timeSheetRecyclerView.adapter = timeSheetAdapter

        EventChangeListner()
    }

    private fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("entries").addSnapshotListener(object :  EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        entryArrayList.add(dc.document.toObject(TimesheetEntry::class.java))
                    }
                }
                timeSheetAdapter.notifyDataSetChanged()
            }
        })
    }
}