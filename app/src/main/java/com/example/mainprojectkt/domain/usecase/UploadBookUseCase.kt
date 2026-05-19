package com.example.mainprojectkt.domain.usecase

import android.net.Uri
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class UploadBookUseCase(private val repository: BARepository) {
    operator fun invoke(book: Book): Flow<Unit> = repository.uploadBook(book)
}