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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.StrokeCap
import com.students.tastyfood.ui.theme.pastelPink
import com.students.tastyfood.ui.theme.textColor
import com.students.tastyfood.ui.theme.white
import com.students.tastyfood.data.local.CategoriesDataStore
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import com.students.tastyfood.R

@Composable
fun HomeScreen(navController: NavController, viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Все") }
    val categoriesState = remember { mutableStateOf(setOf<String>()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuItems = listOf(
        "Главная" to "home",
        "Избранное" to "favorites",
        "Мои рецепты" to "myrecipes",
        "Добавить рецепт" to "addrecipe",
        "Настройки" to "settings"
    )
    LaunchedEffect(Unit) {
        CategoriesDataStore.getCategories(context).collectLatest { cats ->
            categoriesState.value = cats
        }
    }

    val filteredRecipes = recipes
        .filter { selectedCategory == "Все" || it.category == selectedCategory }
        .filter { it.title.contains(searchQuery, ignoreCase = true) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxHeight().background(white)) {
                Spacer(Modifier.height(32.dp))
                menuItems.forEach { (title, route) ->
                    NavigationDrawerItem(
                        label = { Text(title) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (route != "home") navController.navigate(route)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { navController.navigate("addrecipe") }, containerColor = pastelPink) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить рецепт", tint = white)
                    }
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(white)
                            .padding(paddingValues)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, start = 12.dp, end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = null, tint = textColor)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { }) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = pastelPink)
                                }
                            }

                            Text(
                                text = stringResource(R.string.delicious),
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                            Text(
                                text = stringResource(R.string.easy_to_cook),
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
                                    placeholder = { Text(stringResource(R.string.search_perfect), color = Color.Gray) },
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

                            if (filteredRecipes.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            tint = pastelPink,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Нет рецептов", color = pastelPink, fontWeight = FontWeight.Bold)
                                        Text("Добавьте свой первый рецепт или измените фильтры", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredRecipes, key = { it.id }) { recipe ->
                                        AnimatedVisibility(
                                            visible = true,
                                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                        ) {
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
                    }
                }
            )
        }
    )
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
            progress = { recipe.difficulty / 5f },
            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(6.dp)
                                .padding(top = 4.dp, bottom = 2.dp),
            color = pastelPink,
            trackColor = Color.LightGray,
            strokeCap = StrokeCap.Round,
            )
            Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

