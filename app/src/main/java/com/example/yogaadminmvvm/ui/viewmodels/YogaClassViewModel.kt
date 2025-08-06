package com.example.yogaadminmvvm.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.data.repository.YogaClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class YogaClassViewModel @Inject constructor(
    private val repository: YogaClassRepository
) : ViewModel() {

    private val _currentCourseId = MutableStateFlow<Int?>(null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Holds the day of the week for the parent course (e.g., "MONDAY", "TUESDAY")
    private val _courseDayOfWeek = MutableStateFlow<String?>(null)
    val courseDayOfWeek: StateFlow<String?> = _courseDayOfWeek.asStateFlow()

    val yogaClasses: StateFlow<List<YogaClassEntity>> = combine(
        _currentCourseId,
        _searchQuery
    ) { courseId, query ->
        Pair(courseId, query)
    }.flatMapLatest { (courseId, query) ->
        if (courseId == null) {
            MutableStateFlow(emptyList<YogaClassEntity>()) // Or handle error/loading state appropriately
        } else {
            if (query.isBlank()) {
                repository.getClassesForCourse(courseId)
            } else {
                repository.searchClassesByTeacher(courseId, query)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Or SharingStarted.Lazily
        initialValue = emptyList<YogaClassEntity>()
    )

    private val _selectedYogaClass = MutableStateFlow<YogaClassEntity?>(null)
    val selectedYogaClass: StateFlow<YogaClassEntity?> = _selectedYogaClass.asStateFlow()

    fun setCourseDetails(courseId: Int, dayOfWeek: String) {
        _currentCourseId.value = courseId
        _courseDayOfWeek.value = dayOfWeek
        // Initial load will be triggered by combine observing _currentCourseId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadClassDetails(classId: Int) {
        viewModelScope.launch {
            repository.getClassById(classId).collect { classDetail ->
                _selectedYogaClass.value = classDetail
            }
        }
    }

    fun insertClass(yogaClass: YogaClassEntity) {
        viewModelScope.launch {
            repository.insertClass(yogaClass)
        }
    }

    fun updateClass(yogaClass: YogaClassEntity) {
        viewModelScope.launch {
            repository.updateClass(yogaClass)
        }
    }

    fun deleteClass(yogaClass: YogaClassEntity) {
        viewModelScope.launch {
            repository.deleteClass(yogaClass)
        }
    }

    fun clearSelectedClass() {
        _selectedYogaClass.value = null
    }
}
