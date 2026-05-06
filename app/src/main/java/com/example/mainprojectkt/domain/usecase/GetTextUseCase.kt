package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.repository.BARepository

class GetTextUseCase(private val repository: BARepository) {
    operator fun invoke(number: Int) = repository.getText(number)
}