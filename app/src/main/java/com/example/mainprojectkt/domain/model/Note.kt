package com.example.mainprojectkt.domain.model

data class Note (
    val id: Long,
    val bookId: Long,
    val pageNum: Int,
    val text: String
)