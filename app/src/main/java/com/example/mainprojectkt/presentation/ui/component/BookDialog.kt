package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun BookDialog(
    currentTitle: String,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    isAuthor: Boolean
) {
    var newTitle by remember { mutableStateOf(currentTitle) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = if (isAuthor) "Изменить автора" else "Изменить название")
        },
        text = {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text(if (isAuthor) "Автор" else "Название книги") },
                singleLine = true,
                modifier = androidx.compose.ui.Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(newTitle) },
                enabled = newTitle.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}