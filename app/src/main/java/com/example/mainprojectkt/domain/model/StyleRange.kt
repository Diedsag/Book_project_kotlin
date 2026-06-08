package com.example.mainprojectkt.domain.model

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange

data class StyleRange(
    var textRange: TextRange,
    val originalStyle: TextStyleData?,
    val link: String?
) {
    val spanStyle: SpanStyle
        get() = originalStyle?.toSpanStyle() ?: SpanStyle()
}