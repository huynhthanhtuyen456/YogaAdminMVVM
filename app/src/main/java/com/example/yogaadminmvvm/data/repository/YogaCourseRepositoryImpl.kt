package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.dao.YogaCourseDao
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import com.example.yogaadminmvvm.data.remote.FirebaseService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class YogaCourseRepositoryImpl @Inject constructor(
    private val yogaCourseDao: YogaCourseDao,
    private val firebaseService: FirebaseService
) : YogaCourseRepository {

    override fun getAllCourses(): Flow<List<YogaCourseEntity>> {
        return yogaCourseDao.getAllCourses()
    }

    suspend fun getAllCoursesOnce(): List<YogaCourseEntity> {
        return yogaCourseDao.getAllCourses().first()
    }

    override suspend fun insertCourse(course: YogaCourseEntity): Long {
        // `course` object here typically has id=0 (default for autoGenerate)
        val newRowId = yogaCourseDao.insertCourse(course) // newRowId is the auto-generated Int ID as Long

        // Create a new YogaCourseEntity instance that includes the auto-generated ID.
        // This is the instance we'll upload to Firebase.
        val courseWithId = course.copy(id = newRowId.toInt())

        // Upload to Firebase using the course instance that has the correct ID.
        firebaseService.uploadYogaCourse(courseWithId)
        return newRowId
    }

    override suspend fun updateCourse(course: YogaCourseEntity) {
        // course.id is already populated for existing entities.
        yogaCourseDao.updateCourse(course)
        firebaseService.updateYogaCourse(course) 
    }

    override suspend fun deleteCourse(course: YogaCourseEntity) {
        val courseIdString = course.id.toString() // Store before deleting from local
        yogaCourseDao.deleteCourse(course)
        firebaseService.deleteYogaCourse(courseIdString)
    }

    override fun getCourseById(courseId: Int): Flow<YogaCourseEntity?> {
        return yogaCourseDao.getCourseById(courseId)
    }

    override suspend fun clearAllCourses() {
        val allLocalCourses = getAllCoursesOnce()
        allLocalCourses.forEach { localCourse ->
            firebaseService.deleteYogaCourse(localCourse.id.toString())
        }
        yogaCourseDao.clearAllCourses()
    }

    override fun searchByDayOfWeek(day: String): Flow<List<YogaCourseEntity>> {
        return yogaCourseDao.searchByDayOfWeek(day)
    }

    override suspend fun uploadCourseToFirebase(course: YogaCourseEntity) {
        // This method is for uploading an existing local course.
        // Its 'id' field should already be correctly populated from Room.
        firebaseService.uploadYogaCourse(course)
    }
}
