package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.data.local.entity.Ingredient
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel
import io.github.halilozercan.compose.richtext.markdown.Markdown
import io.github.halilozercan.compose.richtext.RichText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: Int, viewModel: RecipeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val recipeState = remember { mutableStateOf<RecipeEntity?>(null) }

    LaunchedEffect(recipeId) {
        viewModel.getRecipeById(recipeId).collectLatest { recipe ->
            recipeState.value = recipe
        }
    }

    val recipe = recipeState.value
    recipe?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }

            Image(
                painter = rememberAsyncImagePainter(model = it.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = it.title,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            // LazyRow ингредиентов
            val allIngredients = listOf(
                Ingredient(1, "Мясо", imageRes = com.students.tastyfood.R.drawable.meat),
                Ingredient(2, "Яйцо", imageRes = com.students.tastyfood.R.drawable.egg),
                Ingredient(3, "Сыр", imageRes = com.students.tastyfood.R.drawable.cheese)
            )
            val recipeIngredients = allIngredients.filter { it.name in recipe.ingredients }
            if (recipeIngredients.isNotEmpty()) {
                Text("Ингредиенты:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                LazyRow(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)) {
                    items(recipeIngredients) { ingredient ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                            if (ingredient.imageRes != null) {
                                Image(
                                    painter = painterResource(id = ingredient.imageRes),
                                    contentDescription = ingredient.name,
                                    modifier = Modifier.height(48.dp)
                                )
                            }
                            Text(ingredient.name, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Визуализация сложности
            LinearProgressIndicator(
                progress = it.difficulty / 5f,
                color = Color(0xFFF96163),
                trackColor = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
            )
            Text("Сложность: ${it.difficulty}/5", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))

            Text(
                text = "Время готовки: ${recipe.cookingTime} мин",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Оценка: ${it.rating} ⭐",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            RichText(modifier = Modifier.padding(16.dp)) {
                Markdown(it.description)
            }

            Button(
                onClick = { navController.navigate("recipeSteps/${it.id}") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Приступить")
            }
        }
    }
}
