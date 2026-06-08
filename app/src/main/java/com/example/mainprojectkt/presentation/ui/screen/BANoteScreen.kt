package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.domain.model.Note

@Composable
fun BANoteScreen(
    note: Note,
    onHome: () -> Unit,
    onBack: () -> Unit,
    onSave: (Note) -> Unit
) {
    var text by remember { mutableStateOf(note.text) }

    Scaffold(
        bottomBar = {
            Column {
                Row {
                    Button({ onHome() }) {
                        Icon(
                            Icons.Default.Home,
                            "На главную"
                        )
                    }
                    Button({ onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Назад"
                        )
                    }
                    Button(
                        onClick = {
                            onSave(note.copy(text = text))
                        },
                        enabled = text != note.text
                    ) {
                        Icon(
                            Icons.Default.Save,
                            "Сохранить"
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                "Заметка",
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, top = 16.dp)
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Текст заметки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(300.dp),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
        }
    }
}