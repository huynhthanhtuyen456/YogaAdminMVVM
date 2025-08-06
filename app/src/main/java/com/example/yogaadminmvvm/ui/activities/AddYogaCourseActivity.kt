package com.example.yogaadminmvvm.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner // Ensure Spinner is imported
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.ui.viewmodels.YogaCourseViewModel
import com.example.yogaadminmvvm.utils.YogaType
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddYogaCourseActivity : AppCompatActivity() {

    // Changed from TextInputEditText to Spinner
    private lateinit var dayOfWeekSpinnerView: Spinner
    private lateinit var timeSpinnerView: Spinner

    private lateinit var capacityEditText: TextInputEditText
    private lateinit var durationEditText: TextInputEditText
    private lateinit var priceEditText: TextInputEditText
    private lateinit var yogaTypeSpinner: Spinner // This one was already a Spinner
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var saveCourseButton: Button

    private val viewModel: YogaCourseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_yoga_course)

        // Initialize views - updated for new Spinners
        dayOfWeekSpinnerView = findViewById(R.id.dayOfWeekSpinner)
        timeSpinnerView = findViewById(R.id.timeSpinner)

        capacityEditText = findViewById(R.id.capacityEditText)
        durationEditText = findViewById(R.id.durationEditText)
        priceEditText = findViewById(R.id.priceEditText)
        yogaTypeSpinner = findViewById(R.id.yogaTypeSpinner) // Existing spinner
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveCourseButton = findViewById(R.id.saveCourseButton)

        // Setup Spinners
        setupDayOfWeekSpinner()
        setupTimeSpinner()
        setupYogaTypeSpinner() // Renamed for consistency, was previously inline

        saveCourseButton.setOnClickListener {
            // Get data from Spinners
            val dayOfWeek = dayOfWeekSpinnerView.selectedItem.toString()
            val time = timeSpinnerView.selectedItem.toString()

            // Get data from EditTexts (no change here)
            val capacityStr = capacityEditText.text.toString().trim()
            val durationStr = durationEditText.text.toString().trim()
            val priceStr = priceEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            // Get data from existing yogaTypeSpinner
            val selectedYogaTypeName = yogaTypeSpinner.selectedItem.toString()

            if (capacityStr.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill capacity, duration, and price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val capacity = capacityStr.toInt()
                val durationMinutes = durationStr.toInt()
                val price = priceStr.toDouble()
                val yogaType = YogaType.valueOf(selectedYogaTypeName)

                val newCourse = YogaCourseEntity(
                    dayOfWeek = dayOfWeek,
                    time = time,
                    capacity = capacity,
                    durationMinutes = durationMinutes,
                    price = price,
                    type = yogaType,
                    description = description.ifEmpty { null }
                )

                viewModel.insertCourse(newCourse)
                Toast.makeText(this, "Course saved!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid number format for capacity, duration, or price.", Toast.LENGTH_LONG).show()
            } catch (e: IllegalArgumentException) { // For YogaType.valueOf
                Toast.makeText(this, "Invalid yoga type selected.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupDayOfWeekSpinner() {
        // ArrayAdapter.createFromResource expects a CharSequence[]
        // resources.getStringArray(R.array.days_of_week_array) returns String[]
        // which is compatible.
        ArrayAdapter.createFromResource(
            this,
            R.array.days_of_week_array, // Your string array resource
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dayOfWeekSpinnerView.adapter = adapter
        }
    }

    private fun setupTimeSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.time_slots_array, // Your string array resource
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeSpinnerView.adapter = adapter
        }
    }

    private fun setupYogaTypeSpinner() {
        // Using .name for robust mapping back to enum, assuming 'name' is what you want to store/retrieve
        val yogaTypes = YogaType.values().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yogaTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yogaTypeSpinner.adapter = adapter
    }
}
