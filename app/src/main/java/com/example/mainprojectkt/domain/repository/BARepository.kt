package com.example.mainprojectkt.domain.repository

import android.net.Uri
import com.example.mainprojectkt.data.model.BookWithPages
import com.example.mainprojectkt.data.model.User
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import kotlinx.coroutines.flow.Flow

interface BARepository {
    suspend fun scanBook(uri: Uri, userId: Long): Long
    fun uploadBook(book: Book): Flow<Unit>
    fun downloadBooks(userId: Long): Flow<List<Book>>
    suspend fun getBookWithPages(bookId: Long): BookWithPages?
    fun updateBook(book: Book): Flow<Unit>
    fun updatePage(pageId: Long, added: List<StyleRange>, deleted: List<StyleRange>): Flow<Unit>
    fun getBookIdsByUser(userId: Long): Flow<List<Long>>
    fun addNote(note: Note): Flow<Long>
    fun getNotes(): Flow<List<Note>>
    fun deleteNote(id: Long): Flow<Unit>
    fun addUser(user: User): Flow<Long>
    fun getUser(email: String): Flow<User?>
    fun deleteBook(book: Book): Flow<Unit>
}