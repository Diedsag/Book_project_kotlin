package com.example.mainprojectkt.data.repository

import android.net.Uri
import android.util.Log
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.PageEntity
import com.example.mainprojectkt.data.local.entity.StyleEntity
import com.example.mainprojectkt.data.model.BookWithPages
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
    id = 0,
    name = name.toString(),
    lastPage = lastPage
)

fun PageWithStyles.toEntity(bookId: Int) = PageEntity(
    id = 0,
    bookId = bookId,
    number = number,
    text = text
)

fun StyleRange.toEntity(pageId: Long) = StyleEntity(
    id = 0,
    end = textRange.end,
    start = textRange.start,
    pageId = pageId,
    style = "test"
)

fun BookEntity.toDomain(pages: List<PageWithStyles>) = Book(
    id = id.toInt(),
    name = name,
    pages = pages,
    lastPage = lastPage
)
fun PageEntity.toDomain() = PageWithStyles(
    number = number,
    text = text,
    styles = listOf()
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
            book.pages.forEach{ page ->
                val pageId = database.pageDao().addPage(page.toEntity(book.id))
                page.styles.forEach { style ->
                    database.styleDao().addStyle(style.toEntity(pageId))
                }
            }
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun downloadBooks(): Flow<List<Book>> {
        return database.bookDao().getBooks().map { list -> list.map {element ->
            val pages = database.pageDao().getPagesByBook(element.id)
            element.toDomain(pages.map { it.toDomain() })
             }
        }
    }

    override suspend fun getBookWithPages(bookId: Long): BookWithPages? {
        return database.bookDao().getBookWithPages(bookId)
    }
}