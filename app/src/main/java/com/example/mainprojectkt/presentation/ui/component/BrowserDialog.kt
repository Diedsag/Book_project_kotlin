package com.example.mainprojectkt.presentation.ui.component

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BrowserDialog(
    quit: () -> Unit,
    complete: (String) -> Unit
) {
    var currentUrl by remember { mutableStateOf("https://www.google.com") }

    AlertDialog(
        onDismissRequest = quit,
        title = { Text("Выберите страницу") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: android.graphics.Bitmap?
                                ) {
                                    url?.let { currentUrl = it }
                                }
                            }

                            loadUrl(currentUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        confirmButton = {
            Button(onClick = { complete(currentUrl) }) {
                Text("Добавить ссылку")
            }
        },
        dismissButton = {
            TextButton(onClick = quit) {
                Text("Отмена")
            }
        }
    )
}