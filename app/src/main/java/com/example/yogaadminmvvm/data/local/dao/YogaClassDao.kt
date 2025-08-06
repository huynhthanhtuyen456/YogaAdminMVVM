package com.example.yogaadminmvvm.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface YogaClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(classInstance: YogaClassDao): Long

    @Update
    suspend fun updateInstance(classInstance: YogaClassDao)

    @Delete
    suspend fun deleteInstance(classInstance: YogaClassDao)

    @Query("SELECT * FROM yoga_classes WHERE id = :id")
    fun getInstanceById(id: Int): Flow<YogaClassDao?>

    @Query("SELECT * FROM yoga_classes WHERE courseId = :courseId ORDER BY date DESC")
    fun getInstancesForCourse(courseId: Int): Flow<List<YogaClassDao>>

    // For requirement d) Search by teacher name (applied to instances)
    @Query("SELECT * FROM yoga_classes WHERE teacherName LIKE :query || '%' ORDER BY date DESC")
    fun searchInstancesByTeacherName(query: String): Flow<List<YogaClassDao>>

    // For requirement d) Search by date (applied to instances)
    @Query("SELECT * FROM yoga_classes WHERE date = :dateString ORDER BY teacherName ASC")
    fun searchInstancesByDate(dateString: String): Flow<List<YogaClassDao>>

    // Could be useful for cleaning up instances if a course is programmatically deleted in a way that doesn't trigger CASCADE
    @Query("DELETE FROM yoga_classes WHERE courseId = :courseId")
    suspend fun deleteInstancesForCourse(courseId: Int): Flow<List<YogaClassDao>>

    @Query("DELETE FROM yoga_classes")
    suspend fun clearAllInstances()
}