package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import kotlinx.coroutines.flow.Flow

interface YogaClassRepository {
    fun getClassesForCourse(courseId: Int): Flow<List<YogaClassEntity>>
    fun searchClassesByTeacher(courseId: Int, teacherNameQuery: String): Flow<List<YogaClassEntity>>
    suspend fun insertClass(yogaClass: YogaClassEntity): Long
    suspend fun updateClass(yogaClass: YogaClassEntity)
    suspend fun deleteClass(yogaClass: YogaClassEntity)
    fun getClassById(classId: Int): Flow<YogaClassEntity?>

    // Advanced Search Methods
    fun searchClassesByDate(date: String): Flow<List<YogaClassEntity>>
    fun searchClassesByDayOfWeek(dayOfWeek: String): Flow<List<YogaClassEntity>>
    fun searchClassesByDateAndDayOfWeek(date: String, dayOfWeek: String): Flow<List<YogaClassEntity>>
}