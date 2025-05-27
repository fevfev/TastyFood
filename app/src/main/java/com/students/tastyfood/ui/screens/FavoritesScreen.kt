package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel

@Composable
fun FavoritesScreen(navController: NavController, viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    val favoriteRecipes = recipes.filter { it.isFavorite }

    if (favoriteRecipes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Нет избранных рецептов", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Добавьте понравившиеся блюда в избранное", fontSize = 13.sp, color = Color.Gray)
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favoriteRecipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("recipeDetail/${recipe.id}") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(recipe.imageUrl),
                            contentDescription = null,
                            modifier = Modifier.size(90.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(recipe.title, fontWeight = FontWeight.Bold)
                            Text("Время: ${recipe.cookingTime} мин", fontSize = 12.sp)
                            Text("★ ${recipe.rating}", fontSize = 12.sp)
                            // Визуализация сложности
                            LinearProgressIndicator(
                                progress = recipe.difficulty / 5f,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(6.dp)
                                    .padding(top = 4.dp, bottom = 2.dp)
                            )
                            Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.toggleFavorite(recipe) }) {
                            Icon(
                                imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
