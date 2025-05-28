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
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import android.content.Context
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import com.students.tastyfood.model.RecipeStep
import com.students.tastyfood.util.RecipeStepSerializer
import com.students.tastyfood.ui.theme.*

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val fileName = "recipe_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream: OutputStream = file.outputStream()
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(navController: NavController, viewModel: RecipeViewModel, onMenuClick: (() -> Unit)? = null) {
    var title by remember { mutableStateOf("") }
    var cookTime by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var selectedIngredients by remember { mutableStateOf(setOf<Int>()) }
    var descriptionMedia by remember { mutableStateOf(listOf<String>()) }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var steps by remember { mutableStateOf(mutableListOf<RecipeStep>()) }
    var newStepDescription by remember { mutableStateOf("") }
    var newStepDuration by remember { mutableStateOf("") }
    var newStepImageUri by remember { mutableStateOf<String?>(null) }
    var newStepVideoUrl by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val ingredients = listOf(
        Ingredient(1, "Мясо", imageRes = R.drawable.meat),
        Ingredient(2, "Яйцо", imageRes = R.drawable.egg),
        Ingredient(3, "Сыр", imageRes = R.drawable.cheese),
    )

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri ->
            val savedPath = saveImageToInternalStorage(context, selectedUri)
            imageUri = savedPath
        }
    }

    val mediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { newUri ->
            val savedPath = saveImageToInternalStorage(context, newUri)
            savedPath?.let {
                descriptionMedia = descriptionMedia + it
            }
        }
    }

    val stepImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri ->
            val savedPath = saveImageToInternalStorage(context, selectedUri)
            newStepImageUri = savedPath
        }
    }

    var selectedCategory by remember { mutableStateOf("Все") }
    var newCategory by remember { mutableStateOf("") }
    val categoriesState = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        CategoriesDataStore.getCategories(context).collectLatest { cats ->
            categoriesState.value = cats
        }
    }

    Scaffold(
        topBar = {
            TastyTopBar(title = "Добавить рецепт", onMenuClick = onMenuClick, onBackClick = if (onMenuClick == null) { { navController.popBackStack() } } else null)
        },
        containerColor = PastelBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(PastelBg, White)
                    )
                )
                .padding(paddingValues)
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
                            tint = PastelPink,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(White, shape = CircleShape)
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
                            .background(White),
                        colors = CardDefaults.cardColors(containerColor = White)
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
                                color = TextColor
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

                            Text(stringResource(R.string.description_media), fontWeight = FontWeight.SemiBold)

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
                                colors = ButtonDefaults.buttonColors(containerColor = PastelBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Добавить файл", color = PastelPink)
                            }

                            Text("Выберите ингредиенты", fontWeight = FontWeight.SemiBold)

                            LazyRow(modifier = Modifier.fillMaxWidth()) {
                                items(ingredients) { ingredient ->
                                    val selected = ingredient.id in selectedIngredients
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(if (selected) PastelPink else PastelBg)
                                            .border(2.dp, if (selected) PastelPink else Color.LightGray, CircleShape)
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

                            Text("Категории", fontWeight = FontWeight.SemiBold)
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
                                    label = { Text(stringResource(R.string.new_category)) },
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

                            Text("Шаги приготовления", fontWeight = FontWeight.SemiBold)
                            Column {
                                steps.forEachIndexed { idx, step ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = PastelBg)
                                    ) {
                                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            if (!step.imageUrl.isNullOrBlank()) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(step.imageUrl),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                                Text(step.description, fontWeight = FontWeight.Medium)
                                                Text("Время: ${step.durationMinutes} мин", fontSize = 12.sp, color = Color.Gray)
                                            }
                                            IconButton(onClick = { steps.removeAt(idx) }) {
                                                Icon(Icons.Default.Favorite, contentDescription = "Удалить", tint = PastelPink)
                                            }
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                    OutlinedTextField(
                                        value = newStepDescription,
                                        onValueChange = { newStepDescription = it },
                                        label = { Text("Описание шага") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = newStepDuration,
                                        onValueChange = { newStepDuration = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Мин.") },
                                        modifier = Modifier.width(70.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = { stepImageLauncher.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = PastelBg)) {
                                        Text("Фото", color = PastelPink)
                                    }
                                }
                                OutlinedTextField(
                                    value = newStepVideoUrl,
                                    onValueChange = { newStepVideoUrl = it },
                                    label = { Text("Ссылка на видео или YouTube (необязательно)") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                )
                                if (newStepImageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(newStepImageUri),
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (newStepDescription.isNotBlank() && newStepDuration.isNotBlank()) {
                                            steps.add(
                                                RecipeStep(
                                                    description = newStepDescription,
                                                    durationMinutes = newStepDuration.toIntOrNull() ?: 5,
                                                    imageUrl = if (newStepVideoUrl.isNotBlank()) newStepVideoUrl else newStepImageUri
                                                )
                                            )
                                            newStepDescription = ""
                                            newStepDuration = ""
                                            newStepImageUri = null
                                            newStepVideoUrl = ""
                                        }
                                    },
                                    enabled = newStepDescription.isNotBlank() && newStepDuration.isNotBlank(),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Добавить шаг")
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
                                                descriptionMedia = steps.map { RecipeStepSerializer.toJson(listOf(it)) },
                                                category = selectedCategory,
                                                steps = steps
                                            )
                                            viewModel.insertRecipe(recipe)
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PastelPink),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(stringResource(R.string.save), color = White)
                            }
                            if (showError) {
                                Text("Заполните все поля", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
