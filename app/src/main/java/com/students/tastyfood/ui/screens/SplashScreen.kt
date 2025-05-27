package com.students.tastyfood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://assets10.lottiefiles.com/packages/lf20_2kscui.json"))
    val progress by animateLottieCompositionAsState(composition, iterations = 1)

    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(400)
            navController.navigate("home") {
                popUpTo(0)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF96163)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(220.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Cook Like a Chef",
                color = Color.White,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

