package com.example.mainprojectkt.domain.model

data class Image (
    val id: Long,
    val pageId: Long,
    val localPath: String,
    val positionIndex: Int,
    val width: Int,
    val height: Int
)