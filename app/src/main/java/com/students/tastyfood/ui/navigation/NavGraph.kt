package com.students.tastyfood.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.students.tastyfood.data.local.db.RecipeDatabase
import com.students.tastyfood.ui.screens.*
import com.students.tastyfood.viewmodel.RecipeViewModel
import com.students.tastyfood.viewmodel.RecipeViewModelFactory

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
    val context = LocalContext.current
    val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()
    val recipeViewModelFactory = RecipeViewModelFactory(recipeDao)
    val recipeViewModel: RecipeViewModel = viewModel(factory = recipeViewModelFactory)
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, recipeViewModel)
        }
        composable("recipeDetail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull() ?: 0
            RecipeDetailScreen(navController, recipeId, recipeViewModel)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController, recipeViewModel)
        }
        composable(Screen.MyRecipes.route) {
            MyRecipesScreen(navController, recipeViewModel)
        }
        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(navController, recipeViewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
