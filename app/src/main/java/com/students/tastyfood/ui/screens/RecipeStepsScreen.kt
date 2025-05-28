package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.model.RecipeStep
import com.students.tastyfood.ui.theme.PastelBg
import com.students.tastyfood.ui.theme.PastelPink
import com.students.tastyfood.ui.theme.TextColor
import com.students.tastyfood.viewmodel.RecipeViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeStepsScreen(navController: NavController, recipeId: Int, stepId: Int, viewModel: RecipeViewModel) {
    var steps by remember { mutableStateOf<List<RecipeStep>>(emptyList()) }
    var isLoaded by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }

    LaunchedEffect(recipeId) {
        viewModel.getRecipeById(recipeId).collectLatest { recipe ->
            steps = recipe?.steps ?: emptyList()
            isLoaded = true
            currentStep = stepId.coerceIn(0, (steps.size - 1).coerceAtLeast(0))
        }
    }

    if (!isLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    if (steps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Нет шагов для этого рецепта", color = PastelPink)
        }
        return
    }
    val step = steps.getOrNull(currentStep) ?: return
    val totalSteps = steps.size
    val totalTime = steps.sumOf { it.durationMinutes }
    val timePassed = steps.take(currentStep + 1).sumOf { it.durationMinutes }
    val timeLeft = totalTime - timePassed
    var imageScale by remember { mutableStateOf(1f) }
    var imageOffsetX by remember { mutableStateOf(0f) }
    var imageOffsetY by remember { mutableStateOf(0f) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Шаг ${currentStep + 1} из $totalSteps", color = TextColor)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PastelPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelBg)
            )
        },
        containerColor = PastelBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PastelBg)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = (currentStep + 1) / totalSteps.toFloat(),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(8.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = PastelPink,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Round,
            )
            Text(
                text = "Осталось времени: $timeLeft мин.",
                color = PastelPink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PastelBg)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            imageScale = (imageScale * zoom).coerceIn(1f, 3f)
                            imageOffsetX += pan.x
                            imageOffsetY += pan.y
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageUrl = step.imageUrl
                when {
                    imageUrl.isNullOrBlank() -> {
                        Box(
                            modifier = Modifier.fillMaxSize().background(PastelBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Нет изображения", color = PastelPink)
                        }
                    }
                    imageUrl.contains("youtube.com") || imageUrl.contains("youtu.be") -> {
                        val videoId = if (imageUrl.contains("v=")) imageUrl.substringAfter("v=").substringBefore("&") else imageUrl.substringAfterLast("/")
                        val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
                        Image(
                            painter = rememberAsyncImagePainter(thumbnailUrl),
                            contentDescription = "YouTube превью",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(imageScale)
                                .offset { androidx.compose.ui.unit.IntOffset(imageOffsetX.toInt(), imageOffsetY.toInt()) }
                        )
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(imageUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelPink),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                        ) {
                            Text("Смотреть на YouTube", color = Color.White)
                        }
                    }
                    imageUrl.endsWith(".mp4") -> {
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(imageUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PastelPink),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("Смотреть видео", color = Color.White)
                        }
                    }
                    else -> {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(imageScale)
                                .offset { androidx.compose.ui.unit.IntOffset(imageOffsetX.toInt(), imageOffsetY.toInt()) }
                        )
                    }
                }
            }
            Text(
                text = step.description,
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(20.dp)
            )
            Text(
                text = "Время на этот шаг: ${step.durationMinutes} мин.",
                color = TextColor,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (currentStep > 0) {
                            currentStep--
                            imageScale = 1f; imageOffsetX = 0f; imageOffsetY = 0f
                        }
                    },
                    enabled = currentStep > 0,
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDF6F9), // светло-розовый
                        contentColor = PastelPink
                    ),
                    modifier = Modifier
                        .shadow(8.dp, CircleShape, ambientColor = Color(0xFFFDF6F9), spotColor = Color(0xFFEAD1DC))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = PastelPink)
                    Text("Назад", color = PastelPink)
                }
                if (currentStep < totalSteps - 1) {
                    Button(
                        onClick = {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                                imageScale = 1f; imageOffsetX = 0f; imageOffsetY = 0f
                            }
                        },
                        enabled = currentStep < totalSteps - 1,
                        shape = CircleShape,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDF6F9),
                            contentColor = PastelPink
                        ),
                        modifier = Modifier
                            .shadow(8.dp, CircleShape)
                    ) {
                        Text("Далее", color = PastelPink)
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Далее", tint = PastelPink, modifier = Modifier.scale(-1f, 1f))
                    }
                } else {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        shape = CircleShape,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelPink,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .shadow(8.dp, CircleShape)
                    ) {
                        Text("К рецепту")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
