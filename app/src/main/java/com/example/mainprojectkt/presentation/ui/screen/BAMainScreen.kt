package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun BAMainScreen(
    toPdf: () -> Unit,
    toBooks: () -> Unit,
    toRegister: () -> Unit,
    toLogin: () -> Unit,
    isLogin: Boolean
){
    Column() {
        if(isLogin){
            Button(onClick = toPdf) {
                Text("Загрузить PDF")
            }
            Button(onClick = toBooks) {
                Text("Выбрать книги")
            }
        }
        Button(onClick = toRegister) {
            Text("Зарегистрироваться")
        }
        Button(onClick = toLogin) {
            Text("Войти")
        }
    }
}