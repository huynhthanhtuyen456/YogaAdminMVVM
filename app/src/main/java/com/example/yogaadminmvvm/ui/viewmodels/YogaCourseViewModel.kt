package com.example.yogaadminmvvm.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.data.repository.YogaCourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YogaCourseViewModel @Inject constructor(
    private val repository: YogaCourseRepository
) : ViewModel() {

    val allCourses: Flow<List<YogaCourseEntity>> = repository.getAllCourses()

    private val _selectedCourse = MutableStateFlow<YogaCourseEntity?>(null)
    val selectedCourse: StateFlow<YogaCourseEntity?> = _selectedCourse

    fun insertCourse(course: YogaCourseEntity) = viewModelScope.launch {
        repository.insertCourse(course)
    }

    fun updateCourse(course: YogaCourseEntity) = viewModelScope.launch {
        repository.updateCourse(course)
    }

    fun deleteCourse(course: YogaCourseEntity) = viewModelScope.launch {
        repository.deleteCourse(course)
    }

    //This existing fun can be used by the activity if it wants to collect the flow itself.
    fun getCourseById(courseId: Int): Flow<YogaCourseEntity?> {
        return repository.getCourseById(courseId)
    }

    // New function to load course details into the StateFlow
    fun loadCourseDetails(courseId: Int) {
        viewModelScope.launch {
            repository.getCourseById(courseId)
                .catch { e -> 
                    // Handle error, e.g., log it or update UI accordingly
                    _selectedCourse.value = null // Reset or signal error
                }
                .collect { course ->
                    _selectedCourse.value = course
                }
        }
    }

    // fun searchByDay(day: String): Flow<List<YogaCourseEntity>> {
    //    return repository.searchByDayOfWeek(day)
    // }
}
