package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class GetUserBooksUseCase(private val repository: BARepository) {
    operator fun invoke(userId: Long): Flow<List<Long>> = repository.getBookIdsByUser(userId)
}