package com.students.tastyfood.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Модель ответа для одного рецепта
data class MealResponse(
    val meals: List<MealDto>?
)

data class MealDto(
    val idMeal: String?,
    val strMeal: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strTags: String?,
    val strYoutube: String?
    // Можно добавить ингредиенты и другие поля при необходимости
)

data class MealShortDto(
    val idMeal: String?,
    val strMeal: String?,
    val strMealThumb: String?
)

data class MealShortResponse(
    val meals: List<MealShortDto>?
)

interface TheMealDbApi {
    // Получить случайный рецепт
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse

    // Поиск по названию
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    // Получить по категории
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealShortResponse

    // Получить подробный рецепт по id
    @GET("lookup.php")
    suspend fun lookupMealById(@Query("i") id: String): MealResponse
}
