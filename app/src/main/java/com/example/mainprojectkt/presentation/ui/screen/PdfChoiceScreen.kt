package com.example.mainprojectkt.presentation.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PdfChoiceScreen(
    change: (Uri) -> Unit,
    onBack: () -> Unit
) {
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        documentUri = it
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            title = { Text("Document Viewer") }
        )
        FilledTonalButton(
            onClick = {
                launcher.launch(arrayOf("application/pdf"))
            }
        ) {
            Text(text = "Select Document")
        }
        Text("Selected: $documentUri")
        Button({change(documentUri!!)
            Log.d("TAG", documentUri.toString())
            onBack()
        }) {
            Text("Save")
        }
    }
}