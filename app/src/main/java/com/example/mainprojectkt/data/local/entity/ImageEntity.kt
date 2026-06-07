package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Images")
data class ImageEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val pageId: Long,
    val imagePath: String,
    val position: Int
)