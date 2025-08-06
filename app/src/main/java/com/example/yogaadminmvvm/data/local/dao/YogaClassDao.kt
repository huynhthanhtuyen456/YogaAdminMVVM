package com.example.yogaadminmvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.yogaadminmvvm.data.local.entities.YogaClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface YogaClassDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(classInstance: YogaClassEntity): Long

    @Update
    suspend fun updateInstance(classInstance: YogaClassEntity)

    @Delete
    suspend fun deleteInstance(classInstance: YogaClassEntity)

    @Query("SELECT * FROM yoga_classes WHERE id = :id")
    fun getInstanceById(id: Int): Flow<YogaClassEntity?>

    @Query("SELECT * FROM yoga_classes WHERE courseId = :courseId ORDER BY date DESC")
    fun getInstancesForCourse(courseId: Int): Flow<List<YogaClassEntity>>

    // Updated to search by teacher name within a specific course
    @Query("SELECT * FROM yoga_classes WHERE courseId = :courseId AND teacherName LIKE :query || '%' ORDER BY date DESC")
    fun searchInstancesByTeacherName(courseId: Int, query: String): Flow<List<YogaClassEntity>>

    // For requirement d) Search by date (applied to instances)
    @Query("SELECT * FROM yoga_classes WHERE date = :dateString ORDER BY teacherName ASC")
    fun searchInstancesByDate(dateString: String): Flow<List<YogaClassEntity>>

    @Query("SELECT * FROM yoga_classes WHERE dayOfWeek = :dayOfWeekString ORDER BY teacherName ASC")
    fun searchInstancesByDayOfWeek(dayOfWeekString: String): Flow<List<YogaClassEntity>>

    @Query("SELECT * FROM yoga_classes WHERE date = :dateString AND dayOfWeek = :dayOfWeekString ORDER BY teacherName ASC")
    fun searchInstancesByDateAndDayOfWeek(dateString: String, dayOfWeekString: String): Flow<List<YogaClassEntity>>

    // Could be useful for cleaning up instances if a course is programmatically deleted in a way that doesn't trigger CASCADE
    @Query("DELETE FROM yoga_classes WHERE courseId = :courseId")
    suspend fun deleteInstancesForCourse(courseId: Int) // Return type should be Unit (or nothing) for delete queries that don't return data

    @Query("DELETE FROM yoga_classes")
    suspend fun clearAllInstances()
}