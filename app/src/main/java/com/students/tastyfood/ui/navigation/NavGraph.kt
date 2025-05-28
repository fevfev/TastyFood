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
import com.students.tastyfood.viewmodel.SettingsViewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object RecipeDetail : Screen("recipeDetail/{recipeId}")
    object Favorites : Screen("favorites")
    object MyRecipes : Screen("myrecipes")
    object AddRecipe : Screen("addrecipe")
    object Settings : Screen("settings")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController = rememberNavController(), settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val recipeDao = RecipeDatabase.getDatabase(context).recipeDao()
    val recipeViewModelFactory = RecipeViewModelFactory(recipeDao)
    val recipeViewModel: RecipeViewModel = viewModel(factory = recipeViewModelFactory)
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route) { _ ->
                SplashScreen(navController)
            }
        }
        composable(Screen.Home.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth }.togetherWith(slideOutHorizontally { fullWidth -> -fullWidth })
                }
            ) { _ ->
                HomeScreen(navController, recipeViewModel)
            }
        }
        composable("recipeDetail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull() ?: 0
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth }.togetherWith(slideOutHorizontally { fullWidth -> -fullWidth })
                }
            ) { _ ->
                RecipeDetailScreen(navController, recipeId, recipeViewModel)
            }
        }
        composable(Screen.Favorites.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth }.togetherWith(slideOutHorizontally { fullWidth -> -fullWidth })
                }
            ) { _ ->
                FavoritesScreen(navController, recipeViewModel)
            }
        }
        composable(Screen.MyRecipes.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth }.togetherWith(slideOutHorizontally { fullWidth -> -fullWidth })
                }
            ) { _ ->
                MyRecipesScreen(navController, recipeViewModel)
            }
        }
        composable(Screen.AddRecipe.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route,
                transitionSpec = {
                    slideInHorizontally { fullWidth -> fullWidth }.togetherWith(slideOutHorizontally { fullWidth -> -fullWidth })
                }
            ) { _ ->
                AddRecipeScreen(navController, recipeViewModel)
            }
        }
        composable(Screen.Settings.route) {
            AnimatedContent(targetState = navController.currentBackStackEntry?.destination?.route) { _ ->
                SettingsScreen(navController, settingsViewModel = settingsViewModel)
            }
        }
    }
}
