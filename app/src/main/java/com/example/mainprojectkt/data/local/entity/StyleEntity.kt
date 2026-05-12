package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Styles",
    indices = [Index(value = ["page_id", "id"], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["page_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StyleEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val page_id: Int,
    val start: Int,
    val end: Int,
    val style: String //TODO
)