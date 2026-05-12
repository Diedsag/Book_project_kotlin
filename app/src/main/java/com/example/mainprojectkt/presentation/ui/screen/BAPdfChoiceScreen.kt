package com.example.mainprojectkt.presentation.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BAPdfChoiceScreen(
    change: (Uri) -> Unit,
    onBack: () -> Unit
) {
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        documentUri = it
    }
    Scaffold(
        bottomBar = {
            Column() {
                Button({onBack()}) {
                    Icon(
                        Icons.Default.Home,
                        "На главную"
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            FilledTonalButton(
                onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                }
            ) {
                Text(text = "Выбрать книгу")
            }
            Button(
                {change(documentUri!!)
                onBack() },
                enabled = documentUri != null
            ) {
                Text("Сохранить выбор")
            }
        }
    }
}