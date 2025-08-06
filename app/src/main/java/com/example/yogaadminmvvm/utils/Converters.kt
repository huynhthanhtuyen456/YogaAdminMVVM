package com.example.yogaadminmvvm.utils

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromYogaType(value: YogaType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toYogaType(value: String?): YogaType? {
        return value?.let { YogaType.valueOf(it) }
    }
}
