package com.example.mainprojectkt.domain.model

data class ImageData (
    val id: Long,
    val pageId: Long,
    val localPath: String,
    val position: Int,
    val width: Float,
    val height: Float
)