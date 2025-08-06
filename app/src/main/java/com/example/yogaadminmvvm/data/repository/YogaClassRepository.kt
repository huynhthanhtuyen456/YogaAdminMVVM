package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import kotlinx.coroutines.flow.Flow

interface YogaClassRepository {
    fun getClassesForCourse(courseId: Int): Flow<List<YogaClassEntity>>
    fun searchClassesByTeacher(courseId: Int, teacherNameQuery: String): Flow<List<YogaClassEntity>> // New method
    suspend fun insertClass(yogaClass: YogaClassEntity): Long
    suspend fun updateClass(yogaClass: YogaClassEntity)
    suspend fun deleteClass(yogaClass: YogaClassEntity)
    fun getClassById(classId: Int): Flow<YogaClassEntity?>
}