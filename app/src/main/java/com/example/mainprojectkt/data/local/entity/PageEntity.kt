package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Pages",
    indices = [Index(value = ["bookId", "id"], unique = false)],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val bookId: Int,
    val number: Int,
    val text: String
)