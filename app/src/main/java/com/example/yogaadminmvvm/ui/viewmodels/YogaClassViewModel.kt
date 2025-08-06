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
import kotlinx.coroutines.flow.flowOf
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

    private val _courseDayOfWeek = MutableStateFlow<String?>(null)
    val courseDayOfWeek: StateFlow<String?> = _courseDayOfWeek.asStateFlow()

    // For Advanced Search
    private val _advancedSearchDate = MutableStateFlow<String?>(null)
    private val _advancedSearchDayOfWeek = MutableStateFlow<String?>(null)
    private val _isAdvancedSearchActive = MutableStateFlow(false)
    val isAdvancedSearchActive: StateFlow<Boolean> = _isAdvancedSearchActive.asStateFlow()

    val yogaClasses: StateFlow<List<YogaClassEntity>> = combine(
        _currentCourseId, _searchQuery, _isAdvancedSearchActive, _advancedSearchDate, _advancedSearchDayOfWeek
    ) { courseId, query, isAdvanced, advDate, advDay ->
        Triple(Triple(courseId, query, isAdvanced), advDate, advDay) // Structure for flatMapLatest
    }.flatMapLatest { (params, advDate, advDay) ->
        val (courseId, query, isAdvanced) = params
        if (isAdvanced) {
            when {
                advDate != null && advDay != null -> repository.searchClassesByDateAndDayOfWeek(advDate, advDay)
                advDate != null -> repository.searchClassesByDate(advDate)
                advDay != null -> repository.searchClassesByDayOfWeek(advDay)
                else -> flowOf(emptyList()) // No advanced criteria, return empty or all?
            }
        } else {
            if (courseId == null) {
                flowOf(emptyList<YogaClassEntity>()) // Or handle error/loading state appropriately
            } else {
                if (query.isBlank()) {
                    repository.getClassesForCourse(courseId)
                } else {
                    repository.searchClassesByTeacher(courseId, query)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<YogaClassEntity>()
    )

    private val _selectedYogaClass = MutableStateFlow<YogaClassEntity?>(null)
    val selectedYogaClass: StateFlow<YogaClassEntity?> = _selectedYogaClass.asStateFlow()

    fun setCourseDetails(courseId: Int, dayOfWeek: String) {
        _currentCourseId.value = courseId
        _courseDayOfWeek.value = dayOfWeek
        _isAdvancedSearchActive.value = false // Reset advanced search when setting new course context
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // If user types in main search, clear advanced search
        if (query.isNotBlank() && _isAdvancedSearchActive.value) {
            clearAdvancedSearchCriteria()
        }
    }

    fun performAdvancedSearch(date: String?, dayOfWeek: String?) {
        _advancedSearchDate.value = date
        _advancedSearchDayOfWeek.value = dayOfWeek?.takeIf { it != "Any Day" } // Treat "Any Day" as null
        _isAdvancedSearchActive.value = date != null || (dayOfWeek != null && dayOfWeek != "Any Day")
        _searchQuery.value = "" // Clear regular search query
    }

    private fun clearAdvancedSearchCriteria() {
        _advancedSearchDate.value = null
        _advancedSearchDayOfWeek.value = null
        _isAdvancedSearchActive.value = false
    }

    fun clearAdvancedSearch() {
        clearAdvancedSearchCriteria()
        // May need to trigger a refresh of the original course list if courseId is set
        _currentCourseId.value = _currentCourseId.value // Re-emit to trigger combine
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
