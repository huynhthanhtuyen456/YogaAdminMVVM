package com.example.yogaadminmvvm.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.ui.viewmodels.YogaClassViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddEditYogaClassActivity : AppCompatActivity() {

    private val viewModel: YogaClassViewModel by viewModels()

    private lateinit var editTextClassDate: TextInputEditText
    private lateinit var editTextTeacherName: TextInputEditText
    private lateinit var editTextClassComments: TextInputEditText
    private lateinit var buttonSaveClass: Button

    private var currentCourseId: Int = -1
    private var currentYogaClassId: Int? = null
    private var courseDayOfWeekString: String? = null

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_yoga_class)

        editTextClassDate = findViewById(R.id.editTextClassDate)
        editTextTeacherName = findViewById(R.id.editTextTeacherName)
        editTextClassComments = findViewById(R.id.editTextClassComments)
        buttonSaveClass = findViewById(R.id.buttonSaveClass)

        currentCourseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)
        courseDayOfWeekString = intent.getStringExtra(EXTRA_COURSE_DAY_OF_WEEK)
        if (intent.hasExtra(EXTRA_YOGA_CLASS_ID)) {
            currentYogaClassId = intent.getIntExtra(EXTRA_YOGA_CLASS_ID, 0)
            title = "Edit Class"
            viewModel.loadClassDetails(currentYogaClassId!!)
        } else {
            title = "Add Class"
        }

        if (currentCourseId == -1 || courseDayOfWeekString == null) {
            Toast.makeText(this, "Error: Course ID or Day of Week missing", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupDatePicker()
        observeViewModel()

        buttonSaveClass.setOnClickListener {
            saveYogaClass()
        }
    }

    private fun setupDatePicker() {
        editTextClassDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, {
                _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                editTextClassDate.setText(dateFormat.format(selectedDate.time))
            }, year, month, day).show()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedYogaClass.collectLatest {
                it?.let {
                    if (currentYogaClassId == it.id) { // Ensure this is the class we want to edit
                        editTextClassDate.setText(it.date)
                        editTextTeacherName.setText(it.teacherName)
                        editTextClassComments.setText(it.comments ?: "")
                        // Update calendar to reflect the loaded date for the date picker
                        try {
                            val loadedDate = dateFormat.parse(it.date)
                            loadedDate?.let { calendar.time = loadedDate }
                        } catch (e: Exception) {
                            // Handle date parse error if needed
                        }
                    }
                }
            }
        }
    }

    private fun saveYogaClass() {
        val date = editTextClassDate.text.toString().trim()
        val teacherName = editTextTeacherName.text.toString().trim()
        val comments = editTextClassComments.text.toString().trim()

        if (date.isEmpty()) {
            editTextClassDate.error = "Date is required"
            return
        }
        if (teacherName.isEmpty()) {
            editTextTeacherName.error = "Teacher name is required"
            return
        }

        if (!isValidDateForCourse(date)) {
            editTextClassDate.error = "Date does not match the course's day of the week ($courseDayOfWeekString)"
            Toast.makeText(this, "Date must be a $courseDayOfWeekString", Toast.LENGTH_LONG).show()
            return
        }

        val yogaClass = YogaClassEntity(
            id = currentYogaClassId ?: 0, // If null, Room will autoGenerate for new entry
            courseId = currentCourseId,
            date = date,
            teacherName = teacherName,
            comments = comments.ifEmpty { null }
        )

        if (currentYogaClassId == null) {
            viewModel.insertClass(yogaClass)
            Toast.makeText(this, "Class added", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.updateClass(yogaClass)
            Toast.makeText(this, "Class updated", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun isValidDateForCourse(dateString: String): Boolean {
        if (courseDayOfWeekString == null) return false // Should not happen
        return try {
            val selectedDate = dateFormat.parse(dateString)
            val cal = Calendar.getInstance()
            selectedDate?.let { cal.time = it }

            val expectedDayOfWeekInt = when (courseDayOfWeekString?.uppercase(Locale.ROOT)) {
                "SUNDAY" -> Calendar.SUNDAY
                "MONDAY" -> Calendar.MONDAY
                "TUESDAY" -> Calendar.TUESDAY
                "WEDNESDAY" -> Calendar.WEDNESDAY
                "THURSDAY" -> Calendar.THURSDAY
                "FRIDAY" -> Calendar.FRIDAY
                "SATURDAY" -> Calendar.SATURDAY
                else -> -1 // Invalid course day of week string
            }

            cal.get(Calendar.DAY_OF_WEEK) == expectedDayOfWeekInt
        } catch (e: Exception) {
            false // Date parsing error
        }
    }

    companion object {
        const val EXTRA_COURSE_ID = "extra_course_id"
        const val EXTRA_COURSE_DAY_OF_WEEK = "extra_course_day_of_week"
        const val EXTRA_YOGA_CLASS_ID = "extra_yoga_class_id"
    }
}
