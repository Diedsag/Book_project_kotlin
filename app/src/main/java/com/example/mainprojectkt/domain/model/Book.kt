package com.example.mainprojectkt.domain.model

data class Book (
    val id: Int,
    val name: String?,
    var pages: List<PageWithStyles>,
    var lastPage: Int
)