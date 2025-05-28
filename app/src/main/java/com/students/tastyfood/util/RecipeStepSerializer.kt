package com.students.tastyfood.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.students.tastyfood.model.RecipeStep

object RecipeStepSerializer {
    private val gson = Gson()
    fun toJson(steps: List<RecipeStep>): String = gson.toJson(steps)
    fun fromJson(json: String): List<RecipeStep> =
        gson.fromJson(json, object : TypeToken<List<RecipeStep>>() {}.type)
}

