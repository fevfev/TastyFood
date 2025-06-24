package com.students.tastyfood.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val imageUrl: String?,
    val cookingTime: String,
    val difficulty: Int,
    val description: String,
    val rating: Float,
    val isFavorite: Boolean = false,
    val ingredients: List<String> = emptyList(),
    val descriptionMedia: List<String> = emptyList(),
    val category: String = "Все рецепты",
    val steps: List<com.students.tastyfood.model.RecipeStep> = emptyList()
)

