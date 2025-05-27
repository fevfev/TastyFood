package com.students.tastyfood.data.local.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split("||")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString("||") ?: ""
    }
}

