package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Images",
    indices = [Index(value = ["pageId", "id"], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val pageId: Long,
    val imagePath: String,
    val position: Int,
    val width: Float,
    val height: Float
)