package com.students.tastyfood.model

data class Recipe(
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<RecipeStep> = emptyList()
)
