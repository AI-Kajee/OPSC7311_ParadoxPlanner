package com.example.opsc7311_paradoxplanner_poe.ui.timesheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.opsc7311_paradoxplanner_poe.databinding.FragmentGalleryBinding
import com.example.opsc7311_paradoxplanner_poe.databinding.FragmentTimesheetBinding

class TimesheetFragment:Fragment() {


    private var _binding: FragmentTimesheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var timesheetViewModel: TimeSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        timesheetViewModel = ViewModelProvider(this).get(TimeSheetViewModel::class.java)

        _binding = FragmentTimesheetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize UI components
        val startDateTimeEditText: EditText = binding.startDateTimeEditText
        val endDateTimeEditText: EditText = binding.endDateTimeEditText
        val categorySpinner: Spinner = binding.categorySpinner
        val descriptionEditText: EditText = binding.descriptionEditText
        val photoUrlEditText: EditText = binding.photoUrlEditText
        val saveButton: Button = binding.saveButton

        // Handle save button click
        saveButton.setOnClickListener {
            val startDateTime = startDateTimeEditText.text.toString()
            val endDateTime = endDateTimeEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()
            val photoUrl = photoUrlEditText.text.toString()

            // TODO: Validate inputs and save timesheet entry
            // For now, just show a toast message
            Toast.makeText(context, "Timesheet entry saved.", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}