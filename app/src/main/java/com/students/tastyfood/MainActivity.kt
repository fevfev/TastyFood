package com.students.tastyfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.students.tastyfood.ui.theme.TastyFoodTheme
import com.students.tastyfood.ui.navigation.NavGraph
import com.students.tastyfood.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsViewModel: SettingsViewModel by viewModels()
        setContent {
            TastyFoodTheme(darkTheme = settingsViewModel.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}

