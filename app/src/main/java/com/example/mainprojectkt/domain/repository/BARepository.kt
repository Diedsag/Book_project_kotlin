package com.example.mainprojectkt.domain.repository

import android.net.Uri
import com.example.mainprojectkt.data.model.BookWithPages
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import kotlinx.coroutines.flow.Flow

interface BARepository {
    suspend fun scanBook(uri: Uri, id: Long): Result<Long>
    fun uploadBook(book: Book): Flow<Unit>
    fun downloadBooks(): Flow<List<Book>>
    suspend fun getBookWithPages(bookId: Long): BookWithPages?
    fun updateBook(book: Book): Flow<Unit>
    fun updatePage(bookId: Long, page: PageWithStyles): Flow<Unit>
}