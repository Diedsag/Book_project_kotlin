package com.example.mainprojectkt.domain.model

import android.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp

data class TextStyleData(
    val size: Float = 16f,
    val index: String = "normal",
    val font: String = "Default",
    val bold: Boolean = false,
    val italic: Boolean = false,
    val color: Int = Color.BLACK,
    val backgroundColor: Int? = null
) {
    fun toSpanStyle(): SpanStyle {
        val baselineShift = when (index) {
            "superscript" -> BaselineShift.Superscript
            "subscript" -> BaselineShift.Subscript
            else -> null
        }

        val effectiveSize = when (index) {
            "superscript", "subscript" -> size * 0.75f
            else -> size
        }

        return SpanStyle(
            brush = SolidColor(ComposeColor(color)),
            fontSize = effectiveSize.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            background = backgroundColor?.let { ComposeColor(it) } ?: ComposeColor.Transparent,
            baselineShift = baselineShift
        )
    }
}