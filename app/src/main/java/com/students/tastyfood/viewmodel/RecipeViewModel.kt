package com.students.tastyfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val recipes: StateFlow<List<RecipeEntity>> = _recipes.asStateFlow()

    private val _favorites = MutableStateFlow<List<RecipeEntity>>(emptyList())
    val favorites: StateFlow<List<RecipeEntity>> = _favorites.asStateFlow()

    init {
        loadRecipes()
        loadFavorites()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getAllRecipes().collect { list ->
                _recipes.update { list }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteRecipes().collect { list ->
                _favorites.update { list }
            }
        }
    }

    fun addRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }

    fun toggleFavorite(recipe: RecipeEntity) {
        val updated = recipe.copy(isFavorite = !recipe.isFavorite)
        viewModelScope.launch {
            repository.updateRecipe(updated)
        }
    }

    fun getRecipeById(id: Int, onResult: (RecipeEntity?) -> Unit) {
        viewModelScope.launch {
            val result = repository.getRecipeById(id)
            onResult(result)
        }
    }
}
