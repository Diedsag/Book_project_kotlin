package com.example.mainprojectkt.domain.model

data class PageWithStyles (
    val number: Int,
    val text: String,
    val styles: List<StyleRange>
)