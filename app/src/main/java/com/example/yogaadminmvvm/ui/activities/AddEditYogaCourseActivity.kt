package com.example.yogaadminmvvm.ui.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.ui.viewmodels.YogaCourseViewModel
import com.example.yogaadminmvvm.utils.YogaType
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditYogaCourseActivity : AppCompatActivity() {
    private lateinit var dayOfWeekSpinnerView: Spinner
    private lateinit var timeSpinnerView: Spinner
    private lateinit var capacityEditText: TextInputEditText
    private lateinit var durationEditText: TextInputEditText
    private lateinit var priceEditText: TextInputEditText
    private lateinit var yogaTypeSpinner: Spinner
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var saveCourseButton: Button

    private val viewModel: YogaCourseViewModel by viewModels()
    private var currentCourseId: Int? = null

    companion object {
        const val EXTRA_COURSE_ID = "extra_course_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_yoga_course)

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar_add_course)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dayOfWeekSpinnerView = findViewById(R.id.dayOfWeekSpinner)
        timeSpinnerView = findViewById(R.id.timeSpinner)
        capacityEditText = findViewById(R.id.capacityEditText)
        durationEditText = findViewById(R.id.durationEditText)
        priceEditText = findViewById(R.id.priceEditText)
        yogaTypeSpinner = findViewById(R.id.yogaTypeSpinner)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveCourseButton = findViewById(R.id.saveCourseButton)

        setupDayOfWeekSpinner()
        setupTimeSpinner()
        setupYogaTypeSpinner()

        if (intent.hasExtra(EXTRA_COURSE_ID)) {
            currentCourseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)
            if (currentCourseId != -1 && currentCourseId != 0) { // Check for valid ID
                viewModel.loadCourseDetails(currentCourseId!!)
                supportActionBar?.title = "Edit Course" // Set title for editing
            } else {
                supportActionBar?.title = "Add New Course" // Set title for adding
                currentCourseId = null // Ensure it's null if not a valid edit ID
            }
        } else {
            supportActionBar?.title = "Add New Course" // Set title for adding
        }


        lifecycleScope.launch {
            viewModel.selectedCourse.collect { course ->
                // Only populate if it's an existing course being loaded
                if (currentCourseId != null && currentCourseId != 0) {
                    course?.let { populateForm(it) }
                }
            }
        }

        saveCourseButton.setOnClickListener {
            saveCourse()
        }
    }

    // Handle Toolbar item selections
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Simpler: just finishes the current activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateForm(course: YogaCourseEntity) {
        // Set Day of Week Spinner
        val daysOfWeek = resources.getStringArray(R.array.days_of_week_array)
        val dayPosition = daysOfWeek.indexOf(course.dayOfWeek)
        if (dayPosition >= 0) {
            dayOfWeekSpinnerView.setSelection(dayPosition)
        }

        // Set Time Spinner
        val timeSlots = resources.getStringArray(R.array.time_slots_array)
        val timePosition = timeSlots.indexOf(course.time)
        if (timePosition >= 0) {
            timeSpinnerView.setSelection(timePosition)
        }

        capacityEditText.setText(course.capacity.toString())
        durationEditText.setText(course.durationMinutes.toString())
        priceEditText.setText(course.price.toString())
        descriptionEditText.setText(course.description ?: "")

        // Set Yoga Type Spinner
        val yogaTypes = YogaType.values().map { it.name }
        val yogaTypePosition = yogaTypes.indexOf(course.type.name)
        if (yogaTypePosition >= 0) {
            yogaTypeSpinner.setSelection(yogaTypePosition)
        }
    }

    private fun saveCourse() {
        val dayOfWeek = dayOfWeekSpinnerView.selectedItem.toString()
        val time = timeSpinnerView.selectedItem.toString()
        val capacityStr = capacityEditText.text.toString().trim()
        val durationStr = durationEditText.text.toString().trim()
        val priceStr = priceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val selectedYogaTypeName = yogaTypeSpinner.selectedItem.toString()

        if (capacityStr.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill capacity, duration, and price", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val capacity = capacityStr.toInt()
            val durationMinutes = durationStr.toInt()
            val price = priceStr.toDouble()
            val yogaType = YogaType.valueOf(selectedYogaTypeName)

            val courseEntity = YogaCourseEntity(
                id = currentCourseId ?: 0, // Use currentCourseId if editing, otherwise 0 for new
                dayOfWeek = dayOfWeek,
                time = time,
                capacity = capacity,
                durationMinutes = durationMinutes,
                price = price,
                type = yogaType,
                description = description.ifEmpty { null }
            )

            if (currentCourseId != null && currentCourseId != 0) {
                viewModel.updateCourse(courseEntity)
                Toast.makeText(this, "Course updated!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertCourse(courseEntity) // Room will generate ID if id is 0
                Toast.makeText(this, "Course saved!", Toast.LENGTH_SHORT).show()
            }
            finish()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid number format for capacity, duration, or price.", Toast.LENGTH_LONG).show()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Invalid yoga type selected.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupDayOfWeekSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.days_of_week_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dayOfWeekSpinnerView.adapter = adapter
        }
    }

    private fun setupTimeSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.time_slots_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeSpinnerView.adapter = adapter
        }
    }

    private fun setupYogaTypeSpinner() {
        val yogaTypes = YogaType.values().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yogaTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yogaTypeSpinner.adapter = adapter
    }
}
