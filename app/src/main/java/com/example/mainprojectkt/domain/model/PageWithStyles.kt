package com.example.mainprojectkt.domain.model

data class PageWithStyles (
    val id: Long,
    val number: Int,
    val text: String,
    val styles: List<StyleRange>,
    val images: List<Image>
)