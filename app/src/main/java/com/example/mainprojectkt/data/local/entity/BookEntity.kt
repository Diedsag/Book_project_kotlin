package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val lastPage: Int
)