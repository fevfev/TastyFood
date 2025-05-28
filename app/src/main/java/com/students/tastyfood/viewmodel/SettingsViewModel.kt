package com.students.tastyfood.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var isDarkTheme by mutableStateOf(false)
    var fontSize by mutableFloatStateOf(16f)
    var isEnglish by mutableStateOf(false)
    // Можно добавить другие настройки
}

