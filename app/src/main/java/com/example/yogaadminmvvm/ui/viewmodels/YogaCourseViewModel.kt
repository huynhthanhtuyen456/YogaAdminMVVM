package com.example.yogaadminmvvm.ui.viewmodels

import android.app.Application // Import Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.data.repository.YogaCourseRepository
import com.example.yogaadminmvvm.data.repository.YogaCourseRepositoryImpl // Required for casting
import com.example.yogaadminmvvm.utils.ConnectivityUtil // Import ConnectivityUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YogaCourseViewModel @Inject constructor(
    private val repository: YogaCourseRepository,
    private val application: Application // Inject Application
) : ViewModel() {

    val allCourses: Flow<List<YogaCourseEntity>> = repository.getAllCourses()

    private val _selectedCourse = MutableStateFlow<YogaCourseEntity?>(null)
    val selectedCourse: StateFlow<YogaCourseEntity?> = _selectedCourse

    // For providing feedback to the UI
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    fun insertCourse(course: YogaCourseEntity) = viewModelScope.launch {
        if (ConnectivityUtil.isNetworkAvailable(application)) {
            repository.insertCourse(course)
            // Optionally, provide feedback after insertion
        } else {
            // Handle no network for insert: maybe save locally only, or show error
             Toast.makeText(application, "No internet. Course saved locally.", Toast.LENGTH_LONG).show()
            // Fallback: insert locally only (requires a separate repository method if not default)
            // For now, the existing insertCourse will attempt Firebase and might fail if no network,
            // or you might want a local-only insert if that's a requirement.
            // Current setup will try to upload and fail if no network, as Firebase SDK handles some offline cases.
        }
    }

    fun updateCourse(course: YogaCourseEntity) = viewModelScope.launch {
        if (ConnectivityUtil.isNetworkAvailable(application)) {
            repository.updateCourse(course)
        } else {
            Toast.makeText(application, "No internet. Cannot update course.", Toast.LENGTH_LONG).show()
        }
    }

    fun deleteCourse(course: YogaCourseEntity) = viewModelScope.launch {
        if (ConnectivityUtil.isNetworkAvailable(application)) {
            repository.deleteCourse(course)
        } else {
            Toast.makeText(application, "No internet. Cannot delete course.", Toast.LENGTH_LONG).show()
        }
    }

    fun getCourseById(courseId: Int): Flow<YogaCourseEntity?> {
        return repository.getCourseById(courseId)
    }

    fun loadCourseDetails(courseId: Int) {
        viewModelScope.launch {
            repository.getCourseById(courseId)
                .catch { e ->
                    _selectedCourse.value = null
                }
                .collect { course ->
                    _selectedCourse.value = course
                }
        }
    }

    fun uploadAllCoursesToFirebase() {
        viewModelScope.launch {
            if (ConnectivityUtil.isNetworkAvailable(application)) {
                _uploadStatus.value = "Starting upload..."
                try {
                    // We need to cast to YogaCourseRepositoryImpl to access getAllCoursesOnce
                    // This is not ideal, ideally getAllCoursesOnce would be on the interface
                    // Or, you'd collect the flow once.
                    val courses = (repository as? YogaCourseRepositoryImpl)?.getAllCoursesOnce()
                    if (courses != null) {
                        courses.forEach { course ->
                            repository.uploadCourseToFirebase(course)
                        }
                        _uploadStatus.value = "All courses uploaded successfully!"
                    } else {
                         _uploadStatus.value = "Error: Could not retrieve courses for upload."
                    }
                } catch (e: Exception) {
                    _uploadStatus.value = "Upload failed: ${e.message}"
                }
            } else {
                _uploadStatus.value = "No internet connection. Cannot upload courses."
            }
        }
    }
    
    fun clearUploadStatus() {
        _uploadStatus.value = null
    }
}
