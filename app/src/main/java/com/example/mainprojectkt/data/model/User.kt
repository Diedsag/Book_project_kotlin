package com.example.mainprojectkt.data.model

data class User (
    val id: Long,
    val email: String,
    val name: String,
    val hashedPassword: String
)