package com.students.tastyfood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.students.tastyfood.data.local.dao.RecipeDao
import com.students.tastyfood.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeDao: RecipeDao) : ViewModel() {

    val recipes: StateFlow<List<RecipeEntity>> = recipeDao.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeDao.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
        }
    }

    fun getRecipeById(id: Int): Flow<RecipeEntity?> {
        return flow {
            emit(recipeDao.getRecipeById(id))
        }
    }
}