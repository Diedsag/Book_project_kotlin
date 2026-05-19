package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class DownloadBooksUseCase(private val repository: BARepository) {
    operator fun invoke(): Flow<List<Book>> {
        return repository.downloadBooks()
    }
}