package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class DeleteNoteUseCase(private val repository: BARepository) {
    operator fun invoke(id: Long): Flow<Unit> = repository.deleteNote(id)
}