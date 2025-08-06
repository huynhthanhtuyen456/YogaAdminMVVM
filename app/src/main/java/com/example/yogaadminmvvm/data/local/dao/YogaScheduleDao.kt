package com.example.yogaadminmvvm.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface YogaScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(classInstance: YogaScheduleDao): Long

    @Update
    suspend fun updateInstance(classInstance: YogaScheduleDao)

    @Delete
    suspend fun deleteInstance(classInstance: YogaScheduleDao)

    @Query("SELECT * FROM yoga_schedules WHERE id = :id")
    fun getInstanceById(id: Int): Flow<YogaScheduleDao?>

    @Query("SELECT * FROM yoga_schedules WHERE courseId = :courseId ORDER BY date DESC")
    fun getInstancesForCourse(courseId: Int): Flow<List<YogaScheduleDao>>

    // For requirement d) Search by teacher name (applied to instances)
    @Query("SELECT * FROM yoga_schedules WHERE teacherName LIKE :query || '%' ORDER BY date DESC")
    fun searchInstancesByTeacherName(query: String): Flow<List<YogaScheduleDao>>

    // For requirement d) Search by date (applied to instances)
    @Query("SELECT * FROM yoga_schedules WHERE date = :dateString ORDER BY teacherName ASC")
    fun searchInstancesByDate(dateString: String): Flow<List<YogaScheduleDao>>

    // Could be useful for cleaning up instances if a course is programmatically deleted in a way that doesn't trigger CASCADE
    @Query("DELETE FROM yoga_schedules WHERE courseId = :courseId")
    suspend fun deleteInstancesForCourse(courseId: Int): Flow<List<YogaScheduleDao>>

    @Query("DELETE FROM yoga_schedules")
    suspend fun clearAllInstances()
}