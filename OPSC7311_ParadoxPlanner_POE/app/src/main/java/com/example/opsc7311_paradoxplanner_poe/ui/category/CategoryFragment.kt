package com.example.opsc7311_paradoxplanner_poe.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.opsc7311_paradoxplanner_poe.R
import com.example.opsc7311_paradoxplanner_poe.databinding.FragmentGalleryBinding

class CategoryFragment:Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryNameEditText: EditText
    private lateinit var createCategoryButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        categoryNameEditText = view.findViewById(R.id.categoryNameEditText)
        createCategoryButton = view.findViewById(R.id.createCategoryButton)

        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        createCategoryButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString()
            if (categoryName.isNotEmpty()) {
                categoryViewModel.createCategory(categoryName)
            }
        }

        return view
    }

}