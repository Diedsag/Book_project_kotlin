package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: BARepository) {
    operator fun invoke(userId: Long): Flow<List<Note>> = repository.getNotes(userId)
}