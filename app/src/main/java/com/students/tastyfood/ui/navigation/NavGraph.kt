package com.students.tastyfood.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.students.tastyfood.ui.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object RecipeDetail : Screen("recipeDetail/{recipeId}")
    object Favorites : Screen("favorites")
    object MyRecipes : Screen("myrecipes")
    object AddRecipe : Screen("addrecipe")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable("recipeDetail/{recipeId}") {
            RecipeDetailScreen(navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController)
        }
        composable(Screen.MyRecipes.route) {
            MyRecipesScreen(navController)
        }
        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
