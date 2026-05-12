package com.example.mainprojectkt.domain.usecase

import android.net.Uri
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class GetBookUseCase(private val repository: BARepository) {
    operator fun invoke(uri: Uri): Flow<Book> = repository.getBook(uri)
}