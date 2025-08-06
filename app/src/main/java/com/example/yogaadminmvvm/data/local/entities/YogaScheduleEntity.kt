package com.example.yogaadminmvvm.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "yoga_schedules",
    foreignKeys = [ForeignKey(
        entity = YogaCourseEntity::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE // If a YogaCourse is deleted, its instances are also deleted
    )],
    indices = [Index(value = ["courseId"])] // Index for faster queries on courseId
)
data class YogaScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: Int,          // Foreign key to YogaCourse
    val date: String,           // e.g., "2023-10-17" (yyyy-MM-dd format)
    val teacherName: String,
    val comments: String? = null
    // You might add isSynced: Boolean = false for cloud sync later
)
