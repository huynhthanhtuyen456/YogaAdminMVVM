package com.example.yogaadminmvvm.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.ui.adapters.YogaCourseAdapter 
import com.example.yogaadminmvvm.ui.viewmodels.YogaCourseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YogaCourseListActivity : AppCompatActivity(), YogaCourseAdapter.OnCourseActionClickListener {

    private val viewModel: YogaCourseViewModel by viewModels()
    private lateinit var yogaCourseAdapter: YogaCourseAdapter 
    private lateinit var recyclerViewCourses: RecyclerView 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_yoga_course_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView() 

        val fabAddCourse: FloatingActionButton = findViewById(R.id.fabAddCourse)
        fabAddCourse.setOnClickListener {
            val intent = Intent(this, AddYogaCourseActivity::class.java)
            startActivity(intent)
        }

        // Observe ViewModel data
        lifecycleScope.launch {
            viewModel.allCourses.collect { courses ->
                yogaCourseAdapter.submitList(courses)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerViewCourses = findViewById(R.id.recyclerViewCourses) 
        yogaCourseAdapter = YogaCourseAdapter(this) // Pass the listener
        recyclerViewCourses.adapter = yogaCourseAdapter
        recyclerViewCourses.layoutManager = LinearLayoutManager(this) 
    }

    override fun onEditCourseClicked(course: YogaCourseEntity) {
        val intent = Intent(this, AddYogaCourseActivity::class.java).apply {
            putExtra(AddYogaCourseActivity.EXTRA_COURSE_ID, course.id)
        }
        startActivity(intent)
    }

    override fun onDeleteCourseClicked(course: YogaCourseEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this yoga course?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteCourse(course)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
