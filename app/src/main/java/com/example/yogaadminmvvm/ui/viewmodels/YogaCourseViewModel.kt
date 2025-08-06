package com.example.yogaadminmvvm.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.data.repository.YogaCourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YogaCourseViewModel @Inject constructor(
    private val repository: YogaCourseRepository
) : ViewModel() {

    // Expose a Flow of all courses for the list activity
    val allCourses: Flow<List<YogaCourseEntity>> = repository.getAllCourses()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insertCourse(course: YogaCourseEntity) = viewModelScope.launch {
        repository.insertCourse(course)
    }

    // You can add other methods here as needed, for example:
    // fun updateCourse(course: YogaCourseEntity) = viewModelScope.launch {
    //     repository.updateCourse(course)
    // }

    // fun deleteCourse(course: YogaCourseEntity) = viewModelScope.launch {
    //     repository.deleteCourse(course)
    // }

    // fun getCourseById(courseId: Int): Flow<YogaCourseEntity?> {
    //    return repository.getCourseById(courseId)
    // }

    // fun searchByDay(day: String): Flow<List<YogaCourseEntity>> {
    //    return repository.searchByDayOfWeek(day)
    // }
}

