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
    var showColorChoicer by remember { mutableStateOf(false) }
    var colorState by remember { mutableStateOf(Color.Green) }
    var colorChosen by remember { mutableIntStateOf(0) }
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
        Button(onClick = { showColorChoicer = true }) {
            Text("Выбрать цвет")
        }
        Box(modifier = Modifier
            .wrapContentSize()
            .verticalScroll(rememberScrollState())) {
            if (showColorChoicer) {
                AlertDialog(
                    onDismissRequest = { showColorChoicer = false },
                    title = { Text("Цвет") },
                    text = {
                        Column {
                            ColorChoicer(colorState){
                                Log.d("TAG", it.toString())
                                colorChosen = it
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                colorState = Color(colorChosen)
                                showColorChoicer = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showColorChoicer = false
                            }
                        ) {
                            Text("Отмена")
                        }
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                )
            }
        }

        Spacer(modifier = Modifier.background(colorState).width(50.dp).height(50.dp))
    }
}