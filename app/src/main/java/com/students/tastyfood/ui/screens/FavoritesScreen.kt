package com.students.tastyfood.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
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
import com.students.tastyfood.ui.theme.pastelPink
import com.students.tastyfood.ui.theme.textColor
import com.students.tastyfood.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, color = textColor) },
        navigationIcon = {
            when {
                onMenuClick != null -> {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню", tint = pastelPink)
                    }
                }
                onBackClick != null -> {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = pastelPink)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = white
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: RecipeViewModel, onMenuClick: (() -> Unit)? = null) {
    val recipes by viewModel.recipes.collectAsState()
    val favoriteRecipes = recipes.filter { it.isFavorite }

    Scaffold(
        topBar = {
            TastyTopBar(title = "Избранное", onMenuClick = onMenuClick, onBackClick = if (onMenuClick == null) { { navController.popBackStack() } } else null)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addrecipe") }, containerColor = pastelPink) {
                Icon(Icons.Default.Add, contentDescription = "Добавить рецепт", tint = white)
            }
        },
        containerColor = white
    ) { paddingValues ->
        if (favoriteRecipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = pastelPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Нет избранных рецептов", color = pastelPink, fontWeight = FontWeight.Bold)
                    Text("Добавьте понравившиеся блюда в избранное", fontSize = 13.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                items(favoriteRecipes, key = { it.id }) { recipe ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    ) {
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
                                    Text(recipe.title, fontWeight = FontWeight.Bold, color = textColor)
                                    Text("Время: ${recipe.cookingTime} мин", fontSize = 12.sp, color = Color.Gray)
                                    Text("★ ${recipe.rating}", fontSize = 12.sp, color = Color.Gray)
                                    LinearProgressIndicator(
                                        progress = { recipe.difficulty / 5f },
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .height(6.dp)
                                            .padding(top = 4.dp, bottom = 2.dp),
                                        color = pastelPink,
                                        trackColor = Color.LightGray,
                                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    )
                                    Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.toggleFavorite(recipe) }) {
                                    Icon(
                                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = pastelPink
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
