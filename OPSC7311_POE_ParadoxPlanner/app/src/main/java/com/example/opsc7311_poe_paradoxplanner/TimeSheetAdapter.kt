package com.example.opsc7311_poe_paradoxplanner

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeSheetAdapter(private val entryList: ArrayList<TimesheetEntry>) :
    RecyclerView.Adapter<TimeSheetAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val entry = entryList[position]
        holder.timeSheetName.text = entry.timesheetName
        holder.category.text = entry.category
        holder.startDate.text = entry.startDate
        holder.endDate.text = entry.endDate

        // Set the click listener for each item
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }

        holder.btnImage.setOnClickListener {
            // Assuming each TimesheetEntry has an imageUri field
            val imageUrl = entry.photoUrl
            // Check if the imageUri is not null
            if (imageUrl!= null) {
                // Create an Intent to start the ViewClickedImage activity
                val intent = Intent(holder.itemView.context, ViewClickedImageActivity::class.java)
                // Pass the imageUri to the ViewClickedImage activity
                intent.putExtra("photoUrl", imageUrl)
                // Start the activity
                holder.itemView.context.startActivity(intent)
            }
        }
        holder.btnTimer.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeSheetName: TextView = itemView.findViewById(R.id.tvTimeSheetName)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val startDate: TextView = itemView.findViewById(R.id.tvStartDate)
        val endDate: TextView = itemView.findViewById(R.id.tvEndDate)
        val btnImage: Button = itemView.findViewById(R.id.btnImage)
        val btnTimer: Button = itemView.findViewById(R.id.btnTimer)
    }
}
