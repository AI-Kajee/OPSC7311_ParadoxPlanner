package com.example.opsc7311_poe_paradoxplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc7311_poe_paradoxplanner.TimesheetEntry

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
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeSheetName: TextView = itemView.findViewById(R.id.tvTimeSheetName)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val startDate: TextView = itemView.findViewById(R.id.tvStartDate)
        val endDate: TextView = itemView.findViewById(R.id.tvEndDate)
    }
}
