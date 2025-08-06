package com.example.yogaadminmvvm.data.model

data class YogaCourse(
    val id: Int,
    val dayOfWeek: String, // e.g., "Monday", "Tuesday"
    val time: String,      // e.g., "10:00", "11:00" (consider LocalTime or store as minutes from midnight if complex time logic is needed)
    val capacity: Int,
    val duration: Int,     // in minutes, e.g., 60
    val price: Double,     // e.g., 10.0
    val type: String,      // e.g., "Flow Yoga", "Aerial Yoga" (consider Enum)
    val description: String? = null
)
