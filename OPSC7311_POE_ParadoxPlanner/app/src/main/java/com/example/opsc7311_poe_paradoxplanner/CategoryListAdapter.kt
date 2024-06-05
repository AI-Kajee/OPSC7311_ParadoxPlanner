package com.example.opsc7311_poe_paradoxplanner

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

    fun updateCategories(targetTotalHours: Double) {
        // Filter the categories based on the targetTotalHours
        val filteredCategories = categoryList.filter { it.totalHours == targetTotalHours }
        // Update the adapter with the filtered list
        categoryList.clear()
        categoryList.addAll(filteredCategories)
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onBindViewHolder(holder: CategoryListAdapter.MyViewHolder, position: Int) {
        val category = categoryList[position]
        holder.category.text = "${category.categoryName} - Total Hours: ${category.totalHours}"
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
