package com.students.tastyfood.data.local.entity

data class Ingredient(
    val id: Int,
    val name: String,
    val imageUrl: String? = null,
    val imageRes: Int? = null
)