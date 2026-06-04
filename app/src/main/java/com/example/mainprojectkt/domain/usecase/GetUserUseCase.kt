package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.repository.BARepository

class GetUserUseCase(private val repository: BARepository) {
    operator fun invoke(email: String) = repository.getUser(email)
}