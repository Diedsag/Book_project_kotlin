package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class UpdateNoteUseCase(private val repository: BARepository) {
    operator fun invoke(note: Note): Flow<Unit> = repository.updateNote(note)
}