package com.students.tastyfood.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.students.tastyfood.model.RecipeStep

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split("||")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString("||") ?: ""
    }

    @TypeConverter
    fun fromRecipeStepList(list: List<RecipeStep>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toRecipeStepList(json: String?): List<RecipeStep> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<RecipeStep>>() {}.type
        return Gson().fromJson(json, type)
    }
}