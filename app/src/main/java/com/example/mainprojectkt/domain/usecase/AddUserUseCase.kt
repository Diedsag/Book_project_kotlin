package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.data.model.User
import com.example.mainprojectkt.domain.repository.BARepository

class AddUserUseCase(private val repository: BARepository) {
    operator fun invoke(user: User) = repository.addUser(user)
}