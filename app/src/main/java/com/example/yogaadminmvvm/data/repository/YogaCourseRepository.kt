package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import kotlinx.coroutines.flow.Flow

interface YogaCourseRepository {

    fun getAllCourses(): Flow<List<YogaCourseEntity>>

    suspend fun insertCourse(course: YogaCourseEntity): Long

    suspend fun updateCourse(course: YogaCourseEntity)

    suspend fun deleteCourse(course: YogaCourseEntity)

    fun getCourseById(courseId: Int): Flow<YogaCourseEntity?>

    suspend fun clearAllCourses()

    fun searchByDayOfWeek(day: String): Flow<List<YogaCourseEntity>>
}

