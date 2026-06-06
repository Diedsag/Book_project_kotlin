package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.presentation.ui.component.ColorChoicer

@Composable
fun BAMainScreen(
    toPdf: () -> Unit,
    toBooks: () -> Unit,
    toRegister: () -> Unit,
    toLogin: () -> Unit,
    isLogin: Boolean
){
    Column() {
        if (isLogin) {
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