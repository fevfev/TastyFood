package com.students.tastyfood.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.StrokeCap
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
fun TastyTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, color = TextColor) },
        navigationIcon = {
            when {
                onMenuClick != null -> {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню", tint = PastelPink)
                    }
                }
                onBackClick != null -> {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PastelPink)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(navController: NavController, viewModel: RecipeViewModel, onMenuClick: (() -> Unit)? = null) {
    val myRecipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TastyTopBar(title = "Мои рецепты", onMenuClick = onMenuClick, onBackClick = if (onMenuClick == null) { { navController.popBackStack() } } else null)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addrecipe") }, containerColor = PastelPink) {
                Icon(Icons.Default.Add, contentDescription = "Добавить рецепт", tint = White)
            }
        },
        containerColor = White
    ) { paddingValues ->
        if (myRecipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = PastelPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("У вас нет своих рецептов", color = PastelPink, fontWeight = FontWeight.Bold)
                    Text("Добавьте свой первый рецепт!", fontSize = 13.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myRecipes, key = { it.id }) { recipe ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("recipeDetail/${recipe.id}") },
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Image(
                                    painter = rememberAsyncImagePainter(recipe.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                    Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Добавлен вами", fontSize = 12.sp, color = PastelPink)
                                    LinearProgressIndicator(
                                        progress = { recipe.difficulty / 5f },
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(6.dp)
                                            .padding(top = 4.dp, bottom = 2.dp),
                                        color = PastelPink,
                                        trackColor = Color.LightGray,
                                        strokeCap = StrokeCap.Round,
                                    )
                                    Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
