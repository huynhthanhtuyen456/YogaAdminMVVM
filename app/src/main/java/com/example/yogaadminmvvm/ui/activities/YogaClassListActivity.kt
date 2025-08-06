
package com.example.yogaadminmvvm.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.databinding.ActivityYogaClassListBinding
import com.example.yogaadminmvvm.ui.adapters.YogaClassAdapter
import com.example.yogaadminmvvm.ui.viewmodels.YogaClassViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YogaClassListActivity : AppCompatActivity(), YogaClassAdapter.OnClassActionClickListener {

    private lateinit var binding: ActivityYogaClassListBinding
    private val viewModel: YogaClassViewModel by viewModels()
    private lateinit var yogaClassAdapter: YogaClassAdapter
    private var currentCourseId: Int = -1
    private var courseDayOfWeek: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaClassListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarYogaClassList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentCourseId = intent.getIntExtra(EXTRA_COURSE_ID, -1)
        courseDayOfWeek = intent.getStringExtra(EXTRA_COURSE_DAY_OF_WEEK)

        if (currentCourseId == -1 || courseDayOfWeek == null) {
            finish()
            return
        }

        supportActionBar?.title = "Classes for Course" // TODO: Fetch and display actual course name

        setupRecyclerView()
        setupSearchView()

        viewModel.setCourseDetails(currentCourseId, courseDayOfWeek!!)

        lifecycleScope.launch {
            viewModel.yogaClasses.collectLatest { classes ->
                yogaClassAdapter.submitList(classes)
            }
        }

        binding.fabAddYogaClass.setOnClickListener {
            val intent = Intent(this, AddEditYogaClassActivity::class.java).apply {
                putExtra(AddEditYogaClassActivity.EXTRA_COURSE_ID, currentCourseId)
                putExtra(AddEditYogaClassActivity.EXTRA_COURSE_DAY_OF_WEEK, courseDayOfWeek)
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

    override fun onEditClassClicked(yogaClass: YogaClassEntity) {
        val intent = Intent(this, AddEditYogaClassActivity::class.java).apply {
            putExtra(AddEditYogaClassActivity.EXTRA_COURSE_ID, currentCourseId)
            putExtra(AddEditYogaClassActivity.EXTRA_COURSE_DAY_OF_WEEK, courseDayOfWeek)
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
        onBackPressedDispatcher.onBackPressed()
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
