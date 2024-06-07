package com.example.opsc7311_poe_paradoxplanner

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        holder.duration.text=entry.duration + "hrs"





        // Correctly decode the base64 string to a Bitmap and set it as the ImageView's drawable
        val base64Image = entry.image
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        holder.picImageView.setImageBitmap(decodedImage)

        // Set the click listener for each item
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }

        var isEnlarged = false // Flag to track if the image is enlarged

        holder.picImageView.setOnClickListener {
            isEnlarged =!isEnlarged // Toggle the flag

            //Calculate dimensions in dp
            val context = holder.picImageView.context
            val widthDp = if (isEnlarged) 200 else 50
            val heightDp = if (isEnlarged) 200 else 50

            // Convert dp to pixels
            val density = context.resources.displayMetrics.density
            val widthPx = (widthDp * density).toInt()
            val heightPx = (heightDp * density).toInt()

            // Update LayoutParams
            val layoutParams = holder.picImageView.layoutParams
            layoutParams.width = widthPx
            layoutParams.height = heightPx
            holder.picImageView.layoutParams = layoutParams
        }





    }

    override fun getItemCount(): Int {
        return entryList.size
    }



    override fun onViewRecycled(holder: MyViewHolder) {
        super.onViewRecycled(holder)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeSheetName: TextView = itemView.findViewById(R.id.tvTimeSheetName)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val startDate: TextView = itemView.findViewById(R.id.tvStartDate)
        val picImageView: ImageView = itemView.findViewById(R.id.pictureImageView)
        val duration:TextView=itemView.findViewById(R.id.tvDuration)
    }
}
