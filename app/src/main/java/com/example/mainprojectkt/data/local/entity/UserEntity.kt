package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val hashedPassword: String
)

