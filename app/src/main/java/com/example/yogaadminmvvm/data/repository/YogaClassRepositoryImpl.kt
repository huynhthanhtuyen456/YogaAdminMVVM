package com.example.yogaadminmvvm.data.repository

import com.example.yogaadminmvvm.data.local.dao.YogaClassDao
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class YogaClassRepositoryImpl @Inject constructor(
    private val yogaClassDao: YogaClassDao
) : YogaClassRepository {

    override fun getClassesForCourse(courseId: Int): Flow<List<YogaClassEntity>> {
        return yogaClassDao.getInstancesForCourse(courseId)
    }

    override fun searchClassesByTeacher(courseId: Int, teacherNameQuery: String): Flow<List<YogaClassEntity>> {
        return yogaClassDao.searchInstancesByTeacherName(courseId, teacherNameQuery)
    }

    override suspend fun insertClass(yogaClass: YogaClassEntity): Long {
        return yogaClassDao.insertInstance(yogaClass)
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
}