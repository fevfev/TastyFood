package com.students.tastyfood.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.students.tastyfood.viewmodel.RecipeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText

@Composable
fun RecipeStepScreen(recipeId: Int, navController: NavController, viewModel: RecipeViewModel) {
    val scope = rememberCoroutineScope()
    var recipe by remember { mutableStateOf<com.students.tastyfood.data.local.entity.RecipeEntity?>(null) }

    LaunchedEffect(recipeId) {
        scope.launch {
            viewModel.getRecipeById(recipeId).collectLatest {
                recipe = it
            }
        }
    }

    recipe?.let {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Визуализация сложности
            LinearProgressIndicator(
                progress = { it.difficulty / 5f },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            )
            Text("Сложность: ${it.difficulty}/5", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Image(
                painter = rememberAsyncImagePainter(it.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Описание:")
            RichText(
                modifier = Modifier.padding(8.dp)
            ) {
                Markdown(it.description)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Медиа из descriptionMedia
            if (it.descriptionMedia.isNotEmpty()) {
                Text(text = "Медиа рецепта:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    it.descriptionMedia.forEach { mediaUrl ->
                        when {
                            mediaUrl.contains("youtube.com") || mediaUrl.contains("youtu.be") -> {
                                // YouTube превью и кнопка
                                val videoId =
                                    if (mediaUrl.contains("v=")) mediaUrl.substringAfter("v=").substringBefore("&")
                                    else mediaUrl.substringAfterLast("/")
                                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"
                                Image(
                                    painter = rememberAsyncImagePainter(thumbnailUrl),
                                    contentDescription = "YouTube превью",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, mediaUrl.toUri())
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text("Смотреть на YouTube")
                                }
                            }
                            mediaUrl.endsWith(".mp4") -> {
                                Text("Видео (mp4): $mediaUrl", color = MaterialTheme.colorScheme.primary)
                            }
                            else -> {
                                Image(
                                    painter = rememberAsyncImagePainter(mediaUrl),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
