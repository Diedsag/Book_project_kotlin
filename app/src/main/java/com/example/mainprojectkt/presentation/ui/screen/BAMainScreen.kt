package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun BAMainScreen(
    onClick: (Int) -> Unit,
    toPdf: () -> Unit
){
    var number by remember { mutableStateOf("") }
    Column() {
        TextField(number, { number = it })
        Button(onClick = { Log.d("TAG", "try")
            onClick(number.toInt()) }) {
            Text("Страница")
        }
        Button(onClick = toPdf) {
            Text("Выбрать PDF")
        }
    }
}