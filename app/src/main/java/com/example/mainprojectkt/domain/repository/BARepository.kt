package com.example.mainprojectkt.domain.repository

import android.net.Uri
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import kotlinx.coroutines.flow.Flow

interface BARepository {
    fun scanBook(uri: Uri): Flow<Book>
    fun uploadBook(book: Book): Flow<Unit>
    fun downloadBooks(): Flow<List<Book>>
}