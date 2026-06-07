package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.repository.BARepository

class GetUserUseCase(private val repository: BARepository) {
    operator fun invoke(email: String) = repository.getUserByEmail(email)
    operator fun invoke(id: Long) = repository.getUser(id)
}