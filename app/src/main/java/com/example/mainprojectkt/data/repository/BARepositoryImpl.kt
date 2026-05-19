package com.example.mainprojectkt.data.repository

import android.net.Uri
import android.util.Log
import androidx.room.Database
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.PageEntity
import com.example.mainprojectkt.data.local.entity.StyleEntity
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

fun Book.toEntity() = BookEntity(
    id = id,
    name = name.toString(),
    lastPage = lastPage
)

fun PageWithStyles.toEntity() = PageEntity(
    id = 1,
    book_id = 3,
    number = number,
    text = text
)

fun StyleRange.toEntity() = StyleEntity(
    id = 1,
    end = textRange.end,
    start = textRange.start,
    page_id = 1,
    style = "test"
)

fun BookEntity.toDomain() = Book(
    id = id,
    name = name,
    pages = listOf(),
    lastPage = lastPage
)
class BARepositoryImpl(
    val dataSource: BADataSource,
    val database: BADatabase
): BARepository{
    override fun scanBook(uri: Uri): Flow<Book> {
        return dataSource.scanBook(uri)
    }

    override fun uploadBook(book: Book): Flow<Unit> {
        return flow{
            database.bookDao().addBook(book.toEntity())
            book.pages.forEach{it ->
                database.pageDao().addPage(it.toEntity())
                it.styles.forEach { database.styleDao().addStyle(it.toEntity()) }
            }
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun downloadBooks(): Flow<List<Book>> {
        return database.bookDao().getBooks().map { list -> list.map { it.toDomain() } }
    }
}