package com.example.mainprojectkt.presentation.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BAScanScreen(
    onScan: (Uri, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var scanMode by remember { mutableStateOf(true) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            showConfirmationDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Сканирование книги",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Режим сканирования:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = scanMode,
                            onClick = { scanMode = true }
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Формат документа",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Сохраняет форматирование: шрифты, цвета, размеры, индексы",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !scanMode,
                            onClick = { scanMode = false }
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Единый формат",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Извлекает только текст без форматирования",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { filePickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать PDF файл")
            }

            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Подтверждение") },
                    text = {
                        Column {
                            Text("Режим сканирования: ${if (scanMode) "Формат документа" else "Единый формат"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Начать сканирование?")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmationDialog = false
                                selectedUri?.let { uri ->
                                    onScan(uri, scanMode)
                                    onBack()
                                }
                            }
                        ) {
                            Text("Сканировать")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showConfirmationDialog = false }
                        ) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}