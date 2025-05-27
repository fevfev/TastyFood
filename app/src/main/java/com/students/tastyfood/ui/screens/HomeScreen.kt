package com.students.tastyfood.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel
import androidx.compose.foundation.lazy.grid.*
import com.students.tastyfood.ui.theme.pastelPink
import com.students.tastyfood.ui.theme.textColor
import com.students.tastyfood.ui.theme.white
import com.students.tastyfood.data.local.CategoriesDataStore
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController, viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Все") }
    val categoriesState = remember { mutableStateOf(setOf<String>()) }
    LaunchedEffect(Unit) {
        CategoriesDataStore.getCategories(context).collectLatest { cats ->
            categoriesState.value = cats
        }
    }

    val filteredRecipes = recipes
        .filter { selectedCategory == "Все" || it.category == selectedCategory }
        .filter { it.title.contains(searchQuery, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = textColor)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = pastelPink)
                }
            }

            Text(
                text = "Delicious",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 20.dp)
            )
            Text(
                text = "Easy to cook menu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(start = 20.dp, top = 2.dp, bottom = 4.dp)
            )

            // Search bar styled like card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search your perfect recipe", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = pastelPink)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = white,
                        focusedContainerColor = white,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Text(
                text = "Category",
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 6.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categoriesState.value.toList()) { category ->
                    val selected = category == selectedCategory
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) pastelPink else white
                        ),
                        modifier = Modifier
                            .clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            color = if (selected) white else textColor,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Text(
                text = "Popular",
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRecipes) { recipe ->
                    RecipeCardGridItem(
                        recipe = recipe,
                        onClick = { navController.navigate("recipeDetail/${recipe.id}") },
                        onFavoriteClick = { viewModel.toggleFavorite(recipe) },
                        pastelPink = pastelPink,
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeCardGridItem(
    recipe: RecipeEntity,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    pastelPink: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(recipe.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                )
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = pastelPink
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
            Text("${recipe.cookingTime} min", fontSize = 12.sp, color = Color.Gray)
            Text("★ ${recipe.rating}", fontSize = 12.sp, color = Color.Gray)
            // Визуализация сложности
            LinearProgressIndicator(
                progress = recipe.difficulty / 5f,
                color = pastelPink,
                trackColor = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .padding(top = 4.dp, bottom = 2.dp)
            )
            Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

