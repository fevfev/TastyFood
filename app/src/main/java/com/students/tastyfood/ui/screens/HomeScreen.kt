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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.R
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: RecipeViewModel) {
    val recipes = viewModel.recipes.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tasty Food",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3C444C),
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "\"One cannot think well, love well, sleep well, if one has not dined well.\"",
            fontSize = 16.sp,
            color = Color(0xFF3C444C),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // TODO: Search bar, Categories LazyRow, Filters

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recipes) { recipe ->
                RecipeItem(recipe = recipe, onClick = {
                    navController.navigate("recipeDetail/${recipe.id}")
                }, onFavoriteClick = {
                    viewModel.toggleFavorite(recipe)
                })
            }
        }
    }
}

@Composable
fun RecipeItem(
    recipe: RecipeEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl ?: R.drawable.placeholder),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = recipe.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Время: ${recipe.time} мин")
                Text(text = "Оценка: ${recipe.rating} ⭐")
                // TODO: Прогресс-бар сложности
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}
