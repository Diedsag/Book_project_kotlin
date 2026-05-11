package com.example.mainprojectkt.domain.model

import com.example.mainprojectkt.presentation.ui.screen.StyleRange

data class PageWithStyles (
    val number: Int,
    val text: String,
    val styles: List<StyleRange>
)