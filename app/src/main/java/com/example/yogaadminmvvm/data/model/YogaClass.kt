package com.example.yogaadminmvvm.data.model

data class YogaClass(
    val id: String,
    val courseId: Int,          // Foreign key to YogaCourse
    val date: String,           // e.g., "2023-10-17" (yyyy-MM-dd format)
    val teacherName: String,
    val comments: String? = null
)
