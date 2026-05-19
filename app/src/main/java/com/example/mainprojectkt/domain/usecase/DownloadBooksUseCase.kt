package com.example.mainprojectkt.domain.usecase

import android.util.Log
import com.example.mainprojectkt.data.model.BookWithPages
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class DownloadBooksUseCase(private val repository: BARepository) {
    operator fun invoke(): Flow<List<Book>> {
        return repository.downloadBooks()
    }
    operator fun invoke(id: Long): Flow<BookWithPages?> {
        Log.d("TAG", "rep start")
        return flow {
            emit(repository.getBookWithPages(id))
        }
    }
}