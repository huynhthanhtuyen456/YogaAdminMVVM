package com.example.yogaadminmvvm.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaadminmvvm.ui.activities.AddYogaCourseActivity
import com.example.yogaadminmvvm.R
import com.example.yogaadminmvvm.ui.adapters.YogaCourseAdapter // Uncommented
import com.example.yogaadminmvvm.ui.viewmodels.YogaCourseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YogaCourseListActivity : AppCompatActivity() {

    private val viewModel: YogaCourseViewModel by viewModels()
    private lateinit var yogaCourseAdapter: YogaCourseAdapter // Now a lateinit var
    private lateinit var recyclerViewCourses: RecyclerView // Now a lateinit var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_yoga_course_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView() // Call to setup RecyclerView

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
        recyclerViewCourses = findViewById(R.id.recyclerViewCourses) // Initialize from layout
        yogaCourseAdapter = YogaCourseAdapter() // Initialize adapter
        recyclerViewCourses.adapter = yogaCourseAdapter
        recyclerViewCourses.layoutManager = LinearLayoutManager(this) // Set LayoutManager
    }
}
