package com.students.tastyfood.model

data class RecipeStep(
    val description: String,
    val durationMinutes: Int = 5,
    val imageUrl: String? = null
)

