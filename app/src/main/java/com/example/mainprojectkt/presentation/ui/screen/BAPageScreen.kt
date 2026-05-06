package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BAPageScreen(number: Int, text: String){
    Column() {
        Text(text)
        Text(number.toString())
    }
}