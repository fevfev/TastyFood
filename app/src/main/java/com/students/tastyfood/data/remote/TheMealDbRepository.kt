package com.students.tastyfood.data.remote

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TheMealDbRepository {
    private val api: TheMealDbApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(TheMealDbApi::class.java)
    }

    suspend fun getRandomMeal(): MealDto? {
        return api.getRandomMeal().meals?.firstOrNull()
    }

    suspend fun searchMeals(query: String): List<MealDto> {
        return api.searchMeals(query).meals ?: emptyList()
    }

    suspend fun getMealsByCategory(category: String): List<MealShortDto> {
        return api.getMealsByCategory(category).meals ?: emptyList()
    }

    suspend fun getSomeMealsFromCategory(category: String, count: Int = 2): List<MealDto> {
        val shortList = getMealsByCategory(category)
        println("getSomeMealsFromCategory: shortList.size = ${shortList.size}")
        Log.d("TheMealDbRepository", "Коротких рецептов по категории '$category': ${shortList.size}")
        val selected = if (shortList.size > count) shortList.shuffled().take(count) else shortList
        val result = mutableListOf<MealDto>()
        for (shortMeal in selected) {
            try {
                println("lookupMealById: idMeal = ${shortMeal.idMeal}")
                val detail = api.lookupMealById(shortMeal.idMeal ?: "").meals?.firstOrNull()
                if (detail != null) result.add(detail)
            } catch (e: Exception) {
                Log.e("TheMealDbRepository", "Ошибка при получении подробного рецепта: ${shortMeal.idMeal}", e)
                println("Ошибка при получении подробного рецепта: ${shortMeal.idMeal}, ${e.message}")
            }
        }
        println("getSomeMealsFromCategory: result.size = ${result.size}")
        Log.d("TheMealDbRepository", "Подробных рецептов получено: ${result.size}")
        return result
    }

    override fun toString(): String = super.toString()
}
