package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class DeleteBookUseCase(private val repository: BARepository) {
    operator fun invoke(book: Book): Flow<Unit> = repository.deleteBook(book)
}