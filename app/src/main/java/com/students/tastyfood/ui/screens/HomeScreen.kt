package com.students.tastyfood.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.R
import com.students.tastyfood.data.local.CategoriesDataStore
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.data.remote.MealDto
import com.students.tastyfood.ui.theme.LightPink
import com.students.tastyfood.ui.theme.PastelBg
import com.students.tastyfood.ui.theme.PastelPink
import com.students.tastyfood.ui.theme.SoftPink
import com.students.tastyfood.ui.theme.TextColor
import com.students.tastyfood.viewmodel.RecipeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    val randomMeals by viewModel.randomMeals.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Все") }
    val categoriesState = remember { mutableStateOf(setOf<String>()) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuItems = listOf(
        Triple("Главная", "home", Icons.Default.Home),
        Triple("Избранное", "favorites", Icons.Default.Favorite),
        Triple("Мои рецепты", "myrecipes", Icons.AutoMirrored.Filled.List),
        Triple("Добавить рецепт", "addrecipe", Icons.Default.Add),
        Triple("Настройки", "settings", Icons.Default.Settings)
    )
    var selectedMenu by remember { mutableStateOf("home") }
    var showNetworkLoadButton by remember { mutableStateOf(true) }
    var showMealDialog by remember { mutableStateOf<MealDto?>(null) }
    LaunchedEffect(Unit) {
        CategoriesDataStore.getCategories(context).collectLatest { cats ->
            categoriesState.value = cats
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.loadRandomMeals(20)
    }

    val filteredRecipes = recipes
        .filter { selectedCategory == "Все" || it.category == selectedCategory }
        .filter { it.title.contains(searchQuery, ignoreCase = true) }

    val combinedRecipes = remember(filteredRecipes, randomMeals) {
        val local = filteredRecipes.map { it to null }
        val remote = randomMeals.map { null to it }
        local + remote
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(modifier = Modifier.fillMaxHeight().background(PastelBg)) {
                Spacer(Modifier.height(32.dp))
                menuItems.forEach { (title, route, icon) ->
                    val isSelected = selectedMenu == route
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .then(
                                if (isSelected)
                                    Modifier
                                        .shadow(8.dp, RoundedCornerShape(18.dp))
                                        .background(LightPink, RoundedCornerShape(18.dp))
                                else Modifier
                            )
                    ) {
                        NavigationDrawerItem(
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = if (isSelected) PastelPink else TextColor)
                                    Spacer(Modifier.width(12.dp))
                                    Text(title, color = if (isSelected) PastelPink else TextColor)
                                }
                            },
                            selected = isSelected,
                            onClick = {
                                selectedMenu = route
                                scope.launch { drawerState.close() }
                                if (route != "home") navController.navigate(route)
                            },
                            modifier = Modifier.fillMaxWidth().background(Color.Transparent),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color.Transparent,
                                unselectedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
        content = {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { navController.navigate("addrecipe") }, containerColor = PastelPink) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить рецепт", tint = PastelBg)
                    }
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PastelBg)
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
                                    Icon(Icons.Default.Menu, contentDescription = null, tint = TextColor)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { }) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PastelPink)
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
                                color = TextColor,
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
                                        Icon(Icons.Default.Search, contentDescription = null, tint = PastelPink)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = PastelBg,
                                        focusedContainerColor = PastelBg,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }

                            Text(
                                text = stringResource(R.string.category),
                                fontWeight = FontWeight.SemiBold,
                                color = TextColor,
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
                                            containerColor = if (selected) PastelPink else PastelBg
                                        ),
                                        modifier = Modifier
                                            .clickable { selectedCategory = category }
                                    ) {
                                        Text(
                                            text = category,
                                            color = if (selected) PastelBg else TextColor,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            Text(
                                text = stringResource(R.string.popular),
                                fontWeight = FontWeight.Bold,
                                color = TextColor,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                            )

                            if (showNetworkLoadButton) {
                                Button(
                                    onClick = {
                                        viewModel.loadRandomMeals(2)
                                        showNetworkLoadButton = false
                                    },
                                    modifier = Modifier.padding(16.dp),colors = ButtonDefaults.buttonColors(
                                        containerColor = PastelPink,
                                        contentColor = PastelBg
                                    )
                                ) {
                                    Text("Загрузить сетевые рецепты")
                                }
                            }
                            if (combinedRecipes.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            tint = PastelPink,
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Нет рецептов", color = PastelPink, fontWeight = FontWeight.Bold)
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
                                    items(combinedRecipes) { (localRecipe, remoteRecipe) ->
                                        AnimatedVisibility(
                                            visible = true,
                                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                        ) {
                                            if (localRecipe != null) {
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(220.dp)
                                                        .clickable { navController.navigate("recipeDetail/${localRecipe.id}") }
                                                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                                                    shape = RoundedCornerShape(24.dp),
                                                    elevation = CardDefaults.cardElevation(0.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF6F9))
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Box(modifier = Modifier.fillMaxWidth()) {
                                                            val painter = rememberAsyncImagePainter(localRecipe.imageUrl)
                                                            if (!localRecipe.imageUrl.isNullOrBlank()) {
                                                                Image(
                                                                    painter = painter,
                                                                    contentDescription = null,
                                                                    contentScale = ContentScale.Crop,
                                                                    modifier = Modifier
                                                                        .height(120.dp)
                                                                        .fillMaxWidth()
                                                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                                                )
                                                                if (painter.state is coil3.compose.AsyncImagePainter.State.Error) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .height(120.dp)
                                                                            .fillMaxWidth()
                                                                            .background(Color(0xFFF8EAF3)),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text("Нет фото", color = Color(0xFFB39EB5))
                                                                    }
                                                                }
                                                            } else {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .height(120.dp)
                                                                        .fillMaxWidth()
                                                                        .background(Color(0xFFF8EAF3)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text("Нет фото", color = Color(0xFFB39EB5))
                                                                }
                                                            }
                                                            IconButton(
                                                                onClick = { viewModel.toggleFavorite(localRecipe) },
                                                                modifier = Modifier
                                                                    .align(Alignment.TopEnd)
                                                                    .padding(6.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = if (localRecipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                                    contentDescription = null,
                                                                    tint = PastelPink
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(localRecipe.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextColor)
                                                        Text("${localRecipe.cookingTime} мин", fontSize = 12.sp, color = PastelPink)
                                                        Text("★ ${localRecipe.rating}", fontSize = 12.sp, color = PastelPink)
                                                        LinearProgressIndicator(
                                                            progress = { localRecipe.difficulty / 5f },
                                                            modifier = Modifier
                                                                .fillMaxWidth(0.7f)
                                                                .height(6.dp)
                                                                .padding(top = 4.dp, bottom = 2.dp),
                                                            color = PastelPink,
                                                            trackColor = LightPink,
                                                            strokeCap = StrokeCap.Round,
                                                        )
                                                        Text("Сложность: ${localRecipe.difficulty}/5", fontSize = 11.sp, color = PastelPink)
                                                    }
                                                }
                                            } else if (remoteRecipe != null) {
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(220.dp)
                                                        .clickable { showMealDialog = remoteRecipe }
                                                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                                                    shape = RoundedCornerShape(24.dp),
                                                    elevation = CardDefaults.cardElevation(0.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF6F9))
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Box(modifier = Modifier.fillMaxWidth()) {
                                                            val painter = rememberAsyncImagePainter(remoteRecipe.strMealThumb)
                                                            if (!remoteRecipe.strMealThumb.isNullOrBlank()) {
                                                                Image(
                                                                    painter = painter,
                                                                    contentDescription = null,
                                                                    contentScale = ContentScale.Crop,
                                                                    modifier = Modifier
                                                                        .height(120.dp)
                                                                        .fillMaxWidth()
                                                                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                                                )
                                                                if (painter.state is coil3.compose.AsyncImagePainter.State.Error) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .height(120.dp)
                                                                            .fillMaxWidth()
                                                                            .background(LightPink),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text("Нет фото", color = SoftPink)
                                                                    }
                                                                }
                                                            } else {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .height(120.dp)
                                                                        .fillMaxWidth()
                                                                        .background(LightPink),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Text("Нет фото", color = PastelBg)
                                                                }
                                                            }
                                                            IconButton(
                                                                onClick = {
                                                                    val entity = mealDtoToRecipeEntity(remoteRecipe)
                                                                    viewModel.insertRecipe(entity)
                                                                },
                                                                modifier = Modifier
                                                                    .align(Alignment.TopEnd)
                                                                    .padding(6.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.FavoriteBorder,
                                                                    contentDescription = null,
                                                                    tint = PastelPink
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(remoteRecipe.strMeal ?: "Без названия", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextColor)
                                                        Text(remoteRecipe.strCategory ?: "", fontSize = 12.sp, color = PastelPink)
                                                        Text(remoteRecipe.strArea ?: "", fontSize = 12.sp, color = PastelPink)
                                                    }
                                                }
                                            }
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

    if (showMealDialog != null) {
        AlertDialog(
            onDismissRequest = { showMealDialog = null },
            title = { Text(showMealDialog?.strMeal ?: "Рецепт") },
            text = {
                Column {
                    if (!showMealDialog?.strMealThumb.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(showMealDialog?.strMealThumb),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                    Text(showMealDialog?.strInstructions ?: "Нет описания")
                }
            },
            confirmButton = {
                TextButton(onClick = { showMealDialog = null }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

fun mealDtoToRecipeEntity(meal: MealDto): RecipeEntity {
    return RecipeEntity(
        id = 0,
        title = meal.strMeal ?: "Без названия",
        imageUrl = meal.strMealThumb,
        cookingTime = "?",
        difficulty = 1,
        description = meal.strInstructions ?: "",
        rating = 0f,
        isFavorite = true,
        ingredients = emptyList(),
        descriptionMedia = emptyList(),
        category = meal.strCategory ?: "Сеть"
    )
}
