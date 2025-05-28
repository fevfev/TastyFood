package com.students.tastyfood.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.students.tastyfood.data.local.dao.RecipeDao
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.data.remote.MealDto
import com.students.tastyfood.data.remote.TheMealDbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeDao: RecipeDao) : ViewModel() {

    val recipes: StateFlow<List<RecipeEntity>> = recipeDao.getAllRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val theMealDbRepository = TheMealDbRepository()
    private val _randomMeals = MutableStateFlow<List<MealDto>>(emptyList())
    val randomMeals: StateFlow<List<MealDto>> = _randomMeals

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

    fun insertRecipe(recipe: RecipeEntity) {
        viewModelScope.launch {
            recipeDao.insertRecipe(recipe)
        }
    }

    fun loadRandomMeals(count: Int = 2) {
        println("loadRandomMeals called")
        viewModelScope.launch {
            try {
                Log.d("RecipeViewModel", "Запуск загрузки сетевых рецептов")
                val meals = theMealDbRepository.getSomeMealsFromCategory("Chicken", count)
                Log.d("RecipeViewModel", "Загружено сетевых рецептов: ${meals.size}")
                _randomMeals.value = meals
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Ошибка загрузки сетевых рецептов", e)
                println("Ошибка загрузки сетевых рецептов: ${e.message}")
            }
        }
    }
}
