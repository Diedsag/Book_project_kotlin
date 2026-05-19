package com.example.mainprojectkt.data.repository

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
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

fun PageWithStyles.toEntity(bookId: Long) = PageEntity(
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
    style = if(spanStyle == SpanStyle(background = Color.Yellow)) "yellow" else "blue"
)

fun BookEntity.toDomain(pages: List<PageWithStyles>) = Book(
    id = id,
    name = name,
    pages = pages,
    lastPage = lastPage
)
fun PageEntity.toDomain(styles: List<StyleRange>) = PageWithStyles(
    number = number,
    text = text,
    styles = styles
)
fun StyleEntity.toDomain() = StyleRange(
    textRange = TextRange(start, end),
    spanStyle = SpanStyle(background = if (style == "yellow") Color.Yellow else Color.Blue),
    link = null
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
        return database.bookDao().getBooks().map { list -> list.map {book ->
            val pages = database.pageDao().getPagesByBook(book.id)
            book.toDomain(pages.map { page ->
                val styles = database.styleDao().getStylesByPage(page.id)
                page.toDomain(styles.map{ it.toDomain()}) })
             }
        }
    }

    override suspend fun getBookWithPages(bookId: Long): BookWithPages? {
        return database.bookDao().getBookWithPages(bookId)
    }
}