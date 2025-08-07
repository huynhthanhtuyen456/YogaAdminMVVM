package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.dao.YogaClassDao
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import com.example.yogaadminmvvm.data.remote.FirebaseYogaClass
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class YogaClassRepositoryImpl @Inject constructor(
    private val yogaClassDao: YogaClassDao,
    private val yogaClassFirebase: FirebaseYogaClass
) : YogaClassRepository {

    override fun getClassesForCourse(courseId: Int): Flow<List<YogaClassEntity>> {
        return yogaClassDao.getInstancesForCourse(courseId)
    }

    override fun searchClassesByTeacher(courseId: Int, teacherNameQuery: String): Flow<List<YogaClassEntity>> {
        return yogaClassDao.searchInstancesByTeacherName(courseId, teacherNameQuery)
    }

    override suspend fun insertClass(yogaClass: YogaClassEntity): Long {

        // `course` object here typically has id=0 (default for autoGenerate)
        val newRowId = yogaClassDao.insertInstance(yogaClass) // newRowId is the auto-generated Int ID as Long

        // Create a new YogaCourseEntity instance that includes the auto-generated ID.
        // This is the instance we'll upload to Firebase.
        val courseWithId = yogaClass.copy(id = newRowId.toInt())

        // Upload to Firebase using the course instance that has the correct ID.
        yogaClassFirebase.uploadYogaCourse(courseWithId)
        return newRowId
    }

    override suspend fun updateClass(yogaClass: YogaClassEntity) {
        yogaClassDao.updateInstance(yogaClass)
    }

    override suspend fun deleteClass(yogaClass: YogaClassEntity) {
        yogaClassDao.deleteInstance(yogaClass)
    }

    override fun getClassById(classId: Int): Flow<YogaClassEntity?> {
        return yogaClassDao.getInstanceById(classId)
    }

    // Advanced Search Methods
    override fun searchClassesByDate(date: String): Flow<List<YogaClassEntity>> {
        return yogaClassDao.searchInstancesByDate(date)
    }

    override fun searchClassesByDayOfWeek(dayOfWeek: String): Flow<List<YogaClassEntity>> {
        return yogaClassDao.searchInstancesByDayOfWeek(dayOfWeek)
    }

    override fun searchClassesByDateAndDayOfWeek(date: String, dayOfWeek: String): Flow<List<YogaClassEntity>> {
        return yogaClassDao.searchInstancesByDateAndDayOfWeek(date, dayOfWeek)
    }
}