package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.dao.YogaCourseDao
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class YogaCourseRepositoryImpl @Inject constructor( // Added @Inject here
    private val yogaCourseDao: YogaCourseDao
) : YogaCourseRepository {

    override fun getAllCourses(): Flow<List<YogaCourseEntity>> {
        return yogaCourseDao.getAllCourses()
    }

    override suspend fun insertCourse(course: YogaCourseEntity): Long {
        return yogaCourseDao.insertCourse(course)
    }

    override suspend fun updateCourse(course: YogaCourseEntity) {
        yogaCourseDao.updateCourse(course)
    }

    override suspend fun deleteCourse(course: YogaCourseEntity) {
        yogaCourseDao.deleteCourse(course)
    }

    override fun getCourseById(courseId: Int): Flow<YogaCourseEntity?> {
        return yogaCourseDao.getCourseById(courseId)
    }

    override suspend fun clearAllCourses() {
        yogaCourseDao.clearAllCourses()
    }

    override fun searchByDayOfWeek(day: String): Flow<List<YogaCourseEntity>> {
        return yogaCourseDao.searchByDayOfWeek(day)
    }
}
