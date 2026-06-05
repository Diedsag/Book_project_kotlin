package com.example.mainprojectkt.domain.model

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange

data class StyleRange(
    var textRange: TextRange,
    val spanStyle: SpanStyle,
    val link: String?
)