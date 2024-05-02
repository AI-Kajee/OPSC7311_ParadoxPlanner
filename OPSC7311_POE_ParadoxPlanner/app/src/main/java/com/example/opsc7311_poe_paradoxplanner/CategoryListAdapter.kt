package com.example.opsc7311_poe_paradoxplanner

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryListAdapter(private val categoryList: ArrayList<CategoryDC>, var maxHours: Double) :
    RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryListAdapter.MyViewHolder, position: Int) {
        val category = categoryList[position]
        if (category.totalHours <= maxHours) {
            holder.category.text = "${category.categoryName} - Total Hours: ${category.totalHours}"
        } else {
            holder.category.text = ""
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
