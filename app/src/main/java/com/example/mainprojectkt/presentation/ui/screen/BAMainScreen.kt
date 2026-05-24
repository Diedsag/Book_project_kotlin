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
    onClick: (String) -> Unit,
    toPdf: () -> Unit,
    toBooks: () -> Unit,
    count: () -> Unit,
    upload: () -> Unit,
    check: () -> Unit,
    hasBook: Boolean
){
    var number by remember { mutableStateOf("") }
    Column() {
        TextField(number, { number = it })
        Row() {
            Button(onClick = {
                onClick(number) },
                enabled = hasBook
            ) {
                Text("Страница")
            }
            Text(if (hasBook) "" else "Дождитесь загрузки книги")
        }
        Button(onClick = toPdf) {
            Text("Загрузить PDF")
        }
        Button(onClick = toBooks) {
            Text("Выбрать книги")
        }
        Button(onClick = upload) {
            Text("Загрузить")
        }
        Button(onClick = count) {
            Text("Подсчитать")
        }
        Button(onClick = check) {
            Text("Проверить")
        }
    }
}