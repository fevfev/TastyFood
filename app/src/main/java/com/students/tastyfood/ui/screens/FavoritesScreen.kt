package com.students.tastyfood.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.ui.theme.PastelPink
import com.students.tastyfood.ui.theme.TextColor
import com.students.tastyfood.viewmodel.RecipeViewModel


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
            FloatingActionButton(onClick = { navController.navigate("addrecipe") }, containerColor = PastelPink) {
                Icon(Icons.Default.Add, contentDescription = "Добавить рецепт", tint = White)
            }
        },
        containerColor = White
    ) { paddingValues ->
        if (favoriteRecipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = PastelPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Нет избранных рецептов", color = PastelPink, fontWeight = FontWeight.Bold)
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
                                .clickable { navController.navigate("recipeDetail/${recipe.id}") }
                                .shadow(12.dp, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(0.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF6F9))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(recipe.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(18.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(recipe.title, fontWeight = FontWeight.Bold, color = TextColor)
                                    Text("Время: ${recipe.cookingTime} мин", fontSize = 12.sp, color = PastelPink)
                                    Text("★ ${recipe.rating}", fontSize = 12.sp, color = PastelPink)
                                    LinearProgressIndicator(
                                        progress = { recipe.difficulty / 5f },
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .height(6.dp)
                                            .padding(top = 4.dp, bottom = 2.dp),
                                        color = PastelPink,
                                        trackColor = Color(0xFFF8EAF3),
                                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    )
                                    Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = PastelPink)
                                }
                                IconButton(
                                    onClick = { viewModel.toggleFavorite(recipe) },
                                    modifier = Modifier
                                ) {
                                    Icon(
                                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = PastelPink
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
