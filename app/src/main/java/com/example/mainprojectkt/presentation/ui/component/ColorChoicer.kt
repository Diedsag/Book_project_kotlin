package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun ColorChoicer(
    color: Color,
    onColorChanged: (Int) -> Unit
) {
    var selectedHsvColor by remember { mutableStateOf(HsvColor.from(color)) }

    ClassicColorPicker(
        modifier = Modifier.height(300.dp),
        color = selectedHsvColor,
        showAlphaBar = true,
        onColorChanged = { hsvColor: HsvColor ->
            onColorChanged(hsvColor.toColor().toArgb())
        }
    )
}