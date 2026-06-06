package com.example.mainprojectkt.data.repository

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.core.graphics.toColorInt
import androidx.room.withTransaction
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.data.local.entity.BookEntity
import com.example.mainprojectkt.data.local.entity.NoteEntity
import com.example.mainprojectkt.data.local.entity.PageEntity
import com.example.mainprojectkt.data.local.entity.StyleEntity
import com.example.mainprojectkt.data.local.entity.UserBookEntity
import com.example.mainprojectkt.data.local.entity.UserEntity
import com.example.mainprojectkt.data.model.BookWithPages
import com.example.mainprojectkt.data.model.User
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.domain.repository.BARepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


fun Book.toEntity() = BookEntity(
    id = id,
    name = name ?: "Название не указано",
    author = author,
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
    finish = textRange.end,
    start = textRange.start,
    pageId = pageId,
    style = String.format("#%08X", spanStyle.background.toArgb()),
    link = link
)

fun User.toEntity() = UserEntity(
    id = id,
    email = email,
    name = name,
    hashedPassword = hashedPassword
)

fun Note.toEntity() = NoteEntity(
    id = id,
    pageId = pageId,
    text = text
)

fun BookEntity.toDomain(pages: List<PageWithStyles>) = Book(
    id = id,
    name = name,
    author = author,
    pages = pages,
    lastPage = lastPage
)
fun PageEntity.toDomain(styles: List<StyleRange>) = PageWithStyles(
    id = id,
    number = number,
    text = text,
    styles = styles
)
fun StyleEntity.toDomain() = StyleRange(
    textRange = TextRange(start, finish),
    spanStyle = SpanStyle(background = Color(style.removePrefix("#").toLong(16).toInt())),
    link = link
)

fun UserEntity.toDomain() = User(
    id = id,
    email = email,
    name = name,
    hashedPassword = hashedPassword
)

fun NoteEntity.toDomain() = Note(
    id = id,
    pageId = pageId,
    text = text
)

class BARepositoryImpl(
    val dataSource: BADataSource,
    val database: BADatabase
): BARepository{
    override suspend fun scanBook(uri: Uri, userId: Long): Long {
        val info = dataSource.getBookInfo(uri)
        val pages = dataSource.getPages(uri)
        var bookId = 0L
        database.withTransaction {
            val book = Book(
                id = 0,
                name = info.title ?: "Название не считано",
                author = info.author ?: "Автор не считан",
                pages = emptyList(),
                lastPage = 1
            )
            bookId = database.bookDao().addBook(book.toEntity())

            database.userBookDao().addUserBook(UserBookEntity(0, userId, bookId))

            val pageEntities = pages.map { page ->
                page.toEntity(bookId)
            }.sortedBy { it.number }
            database.pageDao().addPages(pageEntities)
        }
        return bookId
    }

    override fun uploadBook(book: Book): Flow<Unit> {
        return flow{
            val bookId = database.bookDao().addBook(book.toEntity())
            book.pages.forEach{ page ->
                val pageId = database.pageDao().addPage(page.toEntity(bookId))
                page.styles.forEach { style ->
                    database.styleDao().addStyle(style.toEntity(pageId))
                }
            }
            emit(Unit)
        }.flowOn(Dispatchers.IO)
    }

    override fun downloadBooks(userId: Long): Flow<List<Book>> {
        return database.bookDao().getBooksByUser(userId).map { list -> list.map {book ->
            val pages = database.pageDao().getPagesByBook(book.id)
            book.toDomain(pages.map { page ->
                val styles = database.styleDao().getStylesByPage(page.id)
                page.toDomain(styles.map{ it.toDomain()}) })
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getBookWithPages(bookId: Long): BookWithPages? {
        return database.bookDao().getBookWithPages(bookId)
    }

    override fun updateBook(book: Book): Flow<Unit> = flow {
        database.bookDao().updateBook(book.toEntity())
    }

    override fun updatePage(pageId: Long, added: List<StyleRange>, deleted: List<StyleRange>): Flow<Unit> = flow {
        database.withTransaction {
            database.styleDao().addStyles(added.map { it.toEntity(pageId) })
            database.styleDao().deleteStyles(deleted.map { Pair(it.textRange.start, it.textRange.end) })
        }
        emit(Unit)
    }

    override fun getBookIdsByUser(userId: Long): Flow<List<Long>> {
        return database.userBookDao().getBookIdsByUser(userId)
    }

    override fun addNote(note: Note): Flow<Long> {
        return flow{
            val noteId = database.noteDao().addNote(note.toEntity())
            emit(noteId)
        }.flowOn(Dispatchers.IO)
    }

    override fun getNotes(): Flow<List<Note>> {
        return database.noteDao().getNotes().map {notes ->
            notes.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteNote(id: Long) = flow{
        database.noteDao().deleteNote(id)
        emit(Unit)
    }.flowOn(Dispatchers.IO)

    override fun addUser(user: User): Flow<Long> {
        return flow{
            val userId = database.userDao().addUser(user.toEntity())
            emit(userId)
        }.flowOn(Dispatchers.IO)
    }

    override fun getUser(email: String): Flow<User?> {
        return database.userDao().getUserByEmail(email).map{it?.toDomain()}
    }

    override fun deleteBook(book: Book) = flow{
        database.bookDao().deleteBook(book.toEntity())
        emit(Unit)
    }.flowOn(Dispatchers.IO)
}
