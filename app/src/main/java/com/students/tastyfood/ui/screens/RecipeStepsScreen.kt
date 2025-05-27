package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.students.tastyfood.R
import com.students.tastyfood.data.local.entity.RecipeEntity
import com.students.tastyfood.viewmodel.RecipeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeStepsScreen(navController: NavController, recipeId: Int, viewModel: RecipeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val recipeState = remember { mutableStateOf<RecipeEntity?>(null) }

    LaunchedEffect(recipeId) {
        coroutineScope.launch {
            viewModel.getRecipeById(recipeId).collectLatest { recipe ->
                recipeState.value = recipe
            }
        }
    }

    val recipe = recipeState.value
    recipe?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Рецепт") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Визуализация сложности
                LinearProgressIndicator(
                    progress = it.difficulty / 5f,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(6.dp)
                        .padding(bottom = 8.dp)
                )
                Text("Сложность: ${it.difficulty}/5", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Text("1. Замаринуйте креветок.", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.step_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("2. Обжарьте на сковороде до золотистой корочки.", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.step_placeholder2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("3. Добавьте специи соус и рис", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.step_placeholder3),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Готово")
                }
            }
        }
    }
}
