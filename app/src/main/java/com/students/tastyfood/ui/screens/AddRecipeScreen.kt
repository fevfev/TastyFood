package com.students.tastyfood.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.students.tastyfood.data.local.entity.Ingredient
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.R
import com.students.tastyfood.viewmodel.RecipeViewModel
import com.students.tastyfood.data.local.CategoriesDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AddRecipeScreen(navController: NavController, viewModel: RecipeViewModel) {
    var title by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var selectedIngredients by remember { mutableStateOf(setOf<Int>()) }
    var descriptionMedia by remember { mutableStateOf(listOf<String>()) }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pastelPink = Color(0xFFF96163)
    val pastelBg = Color(0xFFF6F4FA)
    val white = Color.White
    val textColor = Color(0xFF3C444C)

    val ingredients = listOf(
        Ingredient(1, "Мясо", imageRes = R.drawable.meat),
        Ingredient(2, "Яйцо", imageRes = R.drawable.egg),
        Ingredient(3, "Сыр", imageRes = R.drawable.cheese),
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri?.toString()
    }

    val mediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { newUri ->
            descriptionMedia = descriptionMedia + newUri.toString()
        }
    }

    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Все") }
    var newCategory by remember { mutableStateOf("") }
    val categoriesState = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        CategoriesDataStore.getCategories(context).collectLatest { cats ->
            categoriesState.value = cats
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(pastelBg, white)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(modifier = Modifier.height(250.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(imageUri)
                                        .build()
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = rememberAsyncImagePainter("https://i.pinimg.com/736x/fb/90/84/fb9084df5b28f78ba9ba7f27de810c70.jpg?text=Recipe+Image"),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = pastelPink,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(white, shape = CircleShape)
                            .padding(8.dp)
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-24).dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(white),
                    colors = CardDefaults.cardColors(containerColor = white)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (title.isBlank()) "Изменить рецепт" else title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Название блюда") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = cookTime,
                            onValueChange = { cookTime = it },
                            label = { Text("Время готовки") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = difficulty,
                            onValueChange = { difficulty = it },
                            label = { Text("Сложность") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Описание блюда") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Описание и медиа:", fontWeight = FontWeight.SemiBold)

                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(descriptionMedia) { mediaUrl ->
                                Image(
                                    painter = rememberAsyncImagePainter(mediaUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Button(
                            onClick = { mediaLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = pastelBg),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Добавить медиа", color = pastelPink)
                        }

                        Text("Выберите ингредиенты:", fontWeight = FontWeight.SemiBold)

                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(ingredients) { ingredient ->
                                val selected = ingredient.id in selectedIngredients
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) pastelPink else pastelBg)
                                        .border(2.dp, if (selected) pastelPink else Color.LightGray, CircleShape)
                                        .clickable {
                                            selectedIngredients = if (selected)
                                                selectedIngredients - ingredient.id
                                            else
                                                selectedIngredients + ingredient.id
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (ingredient.imageRes != null) {
                                        Image(
                                            painter = painterResource(id = ingredient.imageRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = rememberAsyncImagePainter(ingredient.imageUrl),
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }

                        Text("Категория:", fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var expanded by remember { mutableStateOf(false) }
                            OutlinedButton(onClick = { expanded = true }) {
                                Text(selectedCategory)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                categoriesState.value.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = newCategory,
                                onValueChange = { newCategory = it },
                                label = { Text("Новая категория") },
                                singleLine = true,
                                modifier = Modifier.width(140.dp)
                            )
                            Button(
                                onClick = {
                                    if (newCategory.isNotBlank()) {
                                        coroutineScope.launch {
                                            CategoriesDataStore.addCategory(context, newCategory)
                                            selectedCategory = newCategory
                                            newCategory = ""
                                        }
                                    }
                                },
                                enabled = newCategory.isNotBlank(),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text("+")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (title.isBlank() || cookTime.isBlank() || difficulty.isBlank()) {
                                    showError = true
                                } else {
                                    coroutineScope.launch {
                                        val selectedNames = ingredients.filter { it.id in selectedIngredients }.map { it.name }
                                        val recipe = RecipeEntity(
                                            title = title,
                                            imageUrl = imageUri,
                                            cookingTime = cookTime,
                                            difficulty = difficulty.toIntOrNull() ?: 1,
                                            description = description,
                                            rating = 0f,
                                            isFavorite = false,
                                            ingredients = selectedNames,
                                            descriptionMedia = descriptionMedia,
                                            category = selectedCategory
                                        )
                                        viewModel.insertRecipe(recipe)
                                        navController.popBackStack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = pastelPink),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Сохранить рецепт", color = white)
                        }
                        if (showError) {
                            Text("Пожалуйста, заполните все поля", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
