
package com.example.yogaadminmvvm.ui.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.databinding.ActivityYogaClassListBinding
import com.example.yogaadminmvvm.databinding.DialogAdvancedSearchBinding
import com.example.yogaadminmvvm.ui.adapters.YogaClassAdapter
import com.example.yogaadminmvvm.ui.viewmodels.YogaClassViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class YogaClassListActivity : AppCompatActivity(), YogaClassAdapter.OnClassActionClickListener {

    private lateinit var binding: ActivityYogaClassListBinding
    private val viewModel: YogaClassViewModel by viewModels()
    private lateinit var yogaClassAdapter: YogaClassAdapter
    private var currentCourseId: Int = -1
    private var courseDayOfWeek: String? = null

    private var selectedDateForSearch: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaClassListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarYogaClassList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentCourseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)
        courseDayOfWeek = intent.getStringExtra(EXTRA_COURSE_DAY_OF_WEEK)

        if (currentCourseId == -1 && courseDayOfWeek == null) {
            // Allow launching in a global search mode if no course context is provided (optional)
            // For now, let's assume it always needs courseId, but this could be adjusted.
            // If we decide to support global launch, remove this finish() or adjust title logic.
        } else if (currentCourseId == -1 || courseDayOfWeek == null) {
            finish()
            return
        }

        setupRecyclerView()
        setupSearchView()
        setupAdvancedSearchButton()

        if(currentCourseId != -1 && courseDayOfWeek != null) {
            viewModel.setCourseDetails(currentCourseId, courseDayOfWeek!!)
        }

        lifecycleScope.launch {
            viewModel.yogaClasses.collectLatest { classes ->
                yogaClassAdapter.submitList(classes)
            }
        }

        lifecycleScope.launch {
            viewModel.isAdvancedSearchActive.collectLatest { isActive ->
                if (isActive) {
                    supportActionBar?.title = "Advanced Search Results"
                    binding.searchViewTeacher.setQuery("", false) // Clear teacher search
                    binding.searchViewTeacher.clearFocus()
                } else {
                    // Reset to course-specific title if needed
                    binding.textViewParentCourseName.visibility = View.VISIBLE
                    supportActionBar?.title = "Classes for $courseDayOfWeek" // TODO: Use actual course name
                }
                binding.textViewParentCourseName.visibility = if(isActive) View.GONE else View.VISIBLE
            }
        }

        binding.fabAddYogaClass.setOnClickListener {
            val intent = Intent(this, AddEditYogaClassActivity::class.java).apply {
                // Only pass course context if not in advanced search mode or if it makes sense
                if (!viewModel.isAdvancedSearchActive.value && currentCourseId != -1) {
                    putExtra(AddEditYogaClassActivity.EXTRA_COURSE_ID, currentCourseId)
                    putExtra(AddEditYogaClassActivity.EXTRA_COURSE_DAY_OF_WEEK, courseDayOfWeek)
                }
            }
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        yogaClassAdapter = YogaClassAdapter(this)
        binding.recyclerViewYogaClasses.apply {
            adapter = yogaClassAdapter
            layoutManager = LinearLayoutManager(this@YogaClassListActivity)
        }
    }

    private fun setupSearchView() {
        binding.searchViewTeacher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupAdvancedSearchButton() {
        binding.buttonAdvancedSearch.setOnClickListener {
            showAdvancedSearchDialog()
        }
    }

    private fun showAdvancedSearchDialog() {
        val dialogBinding = DialogAdvancedSearchBinding.inflate(LayoutInflater.from(this))
        val dialogView = dialogBinding.root

        val daysOfWeekAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.days_of_week_array_search,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerDayOfWeek.adapter = it
        }

        selectedDateForSearch = null // Reset before opening dialog
        dialogBinding.textViewSelectedDate.visibility = View.GONE

        dialogBinding.buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            selectedDateForSearch?.let { cal -> calendar.time = cal.time }

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDateForSearch = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dialogBinding.textViewSelectedDate.text = "Selected: ${sdf.format(selectedDateForSearch!!.time)}"
                    dialogBinding.textViewSelectedDate.visibility = View.VISIBLE
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Advanced Search")
            .setView(dialogView)
            .setPositiveButton("Search") { _, _ ->
                val selectedDateStr = selectedDateForSearch?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time)
                }
                val selectedDay = dialogBinding.spinnerDayOfWeek.selectedItem.toString()
                viewModel.performAdvancedSearch(selectedDateStr, selectedDay.takeIf { it != getString(R.string.any_day_placeholder) })
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Clear Filters") { _, _ ->
                 viewModel.clearAdvancedSearch()
            }
            .show()
    }

    // Placeholder for R.string.any_day_placeholder - make sure this string exists or use the actual string "Any Day"
    // You'll need to define <string name="any_day_placeholder">Any Day</string> in your strings.xml if not already present.

    override fun onEditClassClicked(yogaClass: YogaClassEntity) {
        val intent = Intent(this, AddEditYogaClassActivity::class.java).apply {
            // Pass original course context if available and relevant for editing
            if (yogaClass.courseId != 0) { // Assuming 0 is not a valid courseId or indicates global context
                putExtra(AddEditYogaClassActivity.EXTRA_COURSE_ID, yogaClass.courseId) // Use class's own courseId
                putExtra(AddEditYogaClassActivity.EXTRA_COURSE_DAY_OF_WEEK, courseDayOfWeek)
                 // Potentially fetch and pass the day of week for this courseId if needed by AddEditYogaClassActivity
            }
            putExtra(AddEditYogaClassActivity.EXTRA_YOGA_CLASS_ID, yogaClass.id)
        }
        startActivity(intent)
    }

    override fun onDeleteClassClicked(yogaClass: YogaClassEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Class")
            .setMessage("Are you sure you want to delete this class scheduled on ${yogaClass.date} with ${yogaClass.teacherName}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteClass(yogaClass)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (viewModel.isAdvancedSearchActive.value) {
            viewModel.clearAdvancedSearch()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }

    companion object {
        const val EXTRA_COURSE_ID = "extra_course_id"
        const val EXTRA_COURSE_DAY_OF_WEEK = "extra_course_day_of_week"

        fun newIntent(context: Context, courseId: Int, courseDayOfWeek: String): Intent {
            return Intent(context, YogaClassListActivity::class.java).apply {
                putExtra(EXTRA_COURSE_ID, courseId)
                putExtra(EXTRA_COURSE_DAY_OF_WEEK, courseDayOfWeek)
            }
        }
    }
}
