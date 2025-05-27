package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.R
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel
import androidx.compose.material3.LinearProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(navController: NavController, viewModel: RecipeViewModel) {
    val myRecipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои рецепты") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addrecipe") }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить рецепт")
            }
        }
    ) { paddingValues ->
        if (myRecipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("У вас нет своих рецептов", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
                items(myRecipes) { recipe ->
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
                                Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Добавлен вами", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                // Визуализация сложности
                                LinearProgressIndicator(
                                    progress = recipe.difficulty / 5f,
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(6.dp)
                                        .padding(top = 4.dp, bottom = 2.dp)
                                )
                                Text("Сложность: ${recipe.difficulty}/5", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
