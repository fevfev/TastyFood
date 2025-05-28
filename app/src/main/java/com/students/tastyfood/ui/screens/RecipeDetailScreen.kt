package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.data.local.entity.Ingredient
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel
import kotlinx.coroutines.flow.collectLatest
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText


@Composable
fun RecipeDetailScreen(navController: NavController, recipeId: Int, viewModel: RecipeViewModel) {
    rememberCoroutineScope()
    val recipeState = remember { mutableStateOf<RecipeEntity?>(null) }

    LaunchedEffect(recipeId) {
        viewModel.getRecipeById(recipeId).collectLatest { recipe ->
            recipeState.value = recipe
        }
    }

    val recipe = recipeState.value
    recipe?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF6FA)) // светлый бело-розовый фон
        ) {
            item {
                // Кнопка "Назад" без белого фона и тени
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color(0xFFF96163))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { /* TODO: обработка избранного */ },
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = com.students.tastyfood.R.drawable.ic_favorite),
                            contentDescription = "В избранное",
                            tint = if (it.isFavorite) Color(0xFFF96163) else Color(0xFFBDBDBD)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp))
                        .background(Color.White, RoundedCornerShape(32.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = it.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    )
                    Text(
                        text = it.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFFF96163),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                val allIngredients = listOf(
                    Ingredient(1, "Мясо", imageRes = com.students.tastyfood.R.drawable.meat),
                    Ingredient(2, "Яйцо", imageRes = com.students.tastyfood.R.drawable.egg),
                    Ingredient(3, "Сыр", imageRes = com.students.tastyfood.R.drawable.cheese)
                )
                val recipeIngredients = allIngredients.filter { it.name in recipe.ingredients }
                if (recipeIngredients.isNotEmpty()) {
                    Text("Ингредиенты:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFFF96163), modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                    LazyRow(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp)) {
                        items(recipeIngredients) { ingredient ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                if (ingredient.imageRes != null) {
                                    Image(
                                        painter = painterResource(id = ingredient.imageRes),
                                        contentDescription = ingredient.name,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                LinearProgressIndicator(
                    progress = { it.difficulty / 5f },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp)
                        .padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
                        .shadow(2.dp, RoundedCornerShape(8.dp)),
                    color = Color(0xFFF96163),
                    trackColor = Color(0xFFFFE0EC),
                    strokeCap = StrokeCap.Round,
                )
                Text("Сложность: ${it.difficulty}/5", fontSize = 13.sp, color = Color(0xFFF96163), modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))

                Text(
                    text = "Время готовки: ${recipe.cookingTime} мин",
                    fontSize = 16.sp,
                    color = Color(0xFFB23A48),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Text(
                    text = "Оценка: ${it.rating} ⭐",
                    fontSize = 16.sp,
                    color = Color(0xFFB23A48),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                val stepsList = it.steps
                Text(
                    text = "Шагов: ${stepsList.size}",
                    color = Color(0xFFB23A48),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                val hasSteps = stepsList.isNotEmpty()
                val stepId = if (hasSteps) 0 else 0

                Text(text = "Описание:", color = Color(0xFFF96163), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                RichText(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Markdown(it.description)
                }

                Button(
                    onClick = {
                        if (hasSteps) {
                            navController.navigate("recipeSteps/${it.id}/$stepId")
                        }
                    },
                    enabled = hasSteps,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF96163),
                        contentColor = Color.White
                    )
                ) {
                    Text("Приступить", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
