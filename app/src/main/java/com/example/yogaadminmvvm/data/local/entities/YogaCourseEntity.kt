package com.example.yogaadminmvvm.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yogaadminmvvm.utils.YogaType


@Entity(tableName = "yoga_courses")
data class YogaCourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayOfWeek: String, // e.g., "Monday", "Tuesday"
    val time: String, // e.g., "10:00" (consider storing as minutes from midnight if complex calcs needed)
    val capacity: Int,
    val durationMinutes: Int, // e.g., 60
    val price: Double,
    val type: YogaType,
    val description: String? = null,
    // You might add isSynced: Boolean = false for cloud sync later
    // val photoPath: String? = null (for extra feature f)
    // val latitude: Double? = null (for extra feature f)
    // val longitude: Double? = null (for extra feature f)
)
