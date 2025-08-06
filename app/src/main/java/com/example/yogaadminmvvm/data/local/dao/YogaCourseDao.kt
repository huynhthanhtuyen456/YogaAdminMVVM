package com.example.yogaadminmvvm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.yogaadminmvvm.data.local.entities.YogaCourseEntity
import kotlinx.coroutines.flow.Flow // For reactive updates

@Dao
interface YogaCourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(yogaCourse: YogaCourseEntity): Long // Returns new rowId

    @Update
    suspend fun updateCourse(yogaCourse: YogaCourseEntity)

    @Delete
    suspend fun deleteCourse(yogaCourse: YogaCourseEntity)

    @Query("SELECT * FROM yoga_courses ORDER BY dayOfWeek, time ASC")
    fun getAllCourses(): Flow<List<YogaCourseEntity>> // Observe changes

    @Query("SELECT * FROM yoga_courses WHERE id = :courseId")
    fun getCourseById(courseId: Int): Flow<YogaCourseEntity?>

    // For requirement b) reset the database (delete all courses)
    @Query("DELETE FROM yoga_courses")
    suspend fun clearAllCourses()

    // For requirement d) Search by teacher name (assuming teacher is part of YogaCourse later, or join with ClassInstance)
    // For now, let's assume we might add a teacher_name to YogaCourse or handle this via ClassInstance.
    // Placeholder for search by teacher - this will need adjustment based on final ClassInstance entity.
    // @Query("SELECT * FROM yoga_courses WHERE teacherName LIKE :query || '%'")
    // fun searchByTeacherName(query: String): Flow<List<YogaCourse>>

    // For requirement d) Search by day of the week
    @Query("SELECT * FROM yoga_courses WHERE dayOfWeek = :day")
    fun searchByDayOfWeek(day: String): Flow<List<YogaCourseEntity>>

    // For requirement d) Search by date (this will likely involve ClassInstance, so placeholder for now)
    // If YogaCourse had a specific date:
    // @Query("SELECT * FROM yoga_courses WHERE date = :dateString")
    // fun searchByDate(dateString: String): Flow<List<YogaCourse>>
}