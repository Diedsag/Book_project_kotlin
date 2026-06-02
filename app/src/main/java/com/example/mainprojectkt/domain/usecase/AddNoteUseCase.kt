package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class AddNoteUseCase(private val repository: BARepository) {
    operator fun invoke(note: Note): Flow<Long> = repository.addNote(note)
}