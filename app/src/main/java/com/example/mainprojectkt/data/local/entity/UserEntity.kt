package com.example.mainprojectkt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Users",
    indices = [Index(value = ["email"], unique = true)])
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val email: String,
    val name: String,
    val hashedPassword: String
)

