package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Pages",
    indices = [Index(value = ["book_id", "id"], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val book_id: Int,
    val number: Int,
    val text: String
)