package com.students.tastyfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.students.tastyfood.ui.theme.textColor
import com.students.tastyfood.ui.theme.white
import com.students.tastyfood.viewmodel.SettingsViewModel
import com.students.tastyfood.R
import com.students.tastyfood.ui.theme.pastelPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel, onMenuClick: (() -> Unit)? = null) {
    val isDarkTheme = settingsViewModel.isDarkTheme
    val fontSize = settingsViewModel.fontSize
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TastyTopBar(
                title = "Настройки",
                onMenuClick = onMenuClick,
                onBackClick = if (onMenuClick == null) { { navController.popBackStack() } } else null
            )
        },
        containerColor = white
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Тёмная тема", color = pastelPink)
                Switch(checked = isDarkTheme, onCheckedChange = { settingsViewModel.isDarkTheme = it }, colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = pastelPink))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Размер шрифта", color = textColor)
                Slider(value = fontSize, colors = SliderDefaults.colors(thumbColor = pastelPink, activeTrackColor = pastelPink) , onValueChange = { settingsViewModel.fontSize = it }, valueRange = 12f..24f)
            }
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Дата рождения") },
                modifier = Modifier.fillMaxWidth()
            )
            // Переключатель языка (switch с флагами)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Язык приложения", color = textColor)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Флаг России
                    Image(
                        painter = painterResource(id = com.students.tastyfood.R.drawable.ic_flag_ru),
                        contentDescription = stringResource(id = R.string.lang_russian),
                        modifier = Modifier.size(24.dp)
                    )
                    Switch(
                        checked = settingsViewModel.isEnglish,
                        onCheckedChange = { settingsViewModel.isEnglish = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = pastelPink,
                            uncheckedThumbColor = white,
                            checkedTrackColor = white,
                            uncheckedTrackColor = pastelPink
                        ),
                        thumbContent = {
                            if (settingsViewModel.isEnglish) {
                                Image(
                                    painter = painterResource(id = com.students.tastyfood.R.drawable.ic_flag_us),
                                    contentDescription = stringResource(id = R.string.lang_english),
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = com.students.tastyfood.R.drawable.ic_flag_ru),
                                    contentDescription = stringResource(id = R.string.lang_russian),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )
                    // Флаг США
                    Image(
                        painter = painterResource(id = com.students.tastyfood.R.drawable.ic_flag_us),
                        contentDescription = stringResource(id = R.string.lang_english),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Включение/отключение уведомлений (заглушка)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Уведомления", color = textColor)
                Switch(checked = true, onCheckedChange = { /* TODO: Реализовать */ },
                    colors = androidx.compose.material3.SwitchDefaults.colors(checkedThumbColor = pastelPink)
                )
            }
            // Кнопка выхода из аккаунта (заглушка)
            Button(
                onClick = { /* TODO: Реализовать выход из аккаунта */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = pastelPink)
            ) {
                Text("Выйти из аккаунта", color = white)
            }
        }
    }
}
