package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.BookApplication
import com.example.mainprojectkt.data.model.User
import com.example.mainprojectkt.data.preferences.PreferencesKeys
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.AddNoteUseCase
import com.example.mainprojectkt.domain.usecase.AddUserUseCase
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetNoteUseCase
import com.example.mainprojectkt.domain.usecase.GetUserBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetUserUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdateBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdatePageUseCase
import com.example.mainprojectkt.domain.usecase.UploadBookUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import java.util.concurrent.atomic.AtomicLong

@OptIn(ExperimentalCoroutinesApi::class)
class BAViewModel (
    val context: Context,
    val scanBookUseCase: ScanBookUseCase,
    val uploadBookUseCase: UploadBookUseCase,
    val downloadBooksUseCase: DownloadBooksUseCase,
    val updateBookUseCase: UpdateBookUseCase,
    val updatePageUseCase: UpdatePageUseCase,
    val getUserBooksUseCase: GetUserBooksUseCase,
    val addNoteUseCase: AddNoteUseCase,
    val getNoteUseCase: GetNoteUseCase,
    val addUserUseCase: AddUserUseCase,
    val getUserUseCase: GetUserUseCase
) : ViewModel(
) {
    var lastId = AtomicLong(0)
    var userId: MutableStateFlow<Long> = MutableStateFlow(-1L)
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    var notesState: MutableStateFlow<List<Note>> = MutableStateFlow(listOf())
    var curBookId = MutableStateFlow<Long?>(null)
    private val dataStore = (context.applicationContext as BookApplication).dataStore
    init{
        viewModelScope.launch {
            dataStore.data.collect { account ->
                userId.value = account[PreferencesKeys.USER_ID] ?: -1
            }
        }

        viewModelScope.launch {
            userId.flatMapLatest { id ->
                if (id == -1L) flowOf(emptyList()) else downloadBooksUseCase(id)
            }.collect { books ->
                booksUiState.value = booksUiState.value.map { state ->
                    if (state is BookUiState.Loading && books.any { it.id == state.id }) {
                        BookUiState.Success(books.find { it.id == state.id }!!)
                    } else state
                }
            }
        }

        viewModelScope.launch {
            userId.flatMapLatest { id ->
                if (id == -1L) flowOf(emptyList()) else getUserBooksUseCase(id)
            }.collect { ids ->
                ids.forEach { id ->
                    if (booksUiState.value.none {
                            (it is BookUiState.Loading && it.id == id) ||
                                    (it is BookUiState.Success && it.book.id == id)
                        }) {
                        booksUiState.value += BookUiState.Loading(id)
                    }
                }
            }
        }

        viewModelScope.launch {
            getNoteUseCase().collect{
                notesState.value = it
            }
        }
    }
    fun scanBook(uri: Uri?) {
        if(userId.value.toInt() != -1) {
            uri?.let { uri ->
                val newId = lastId.incrementAndGet()
                booksUiState.value += BookUiState.Loading(newId)
                viewModelScope.launch(Dispatchers.IO) {
                    scanBookUseCase(uri, userId.value)
                }
            }
        }
    }
    fun changePage(newPage: PageWithStyles){
        booksUiState.value = booksUiState.value.map { element ->
            if (element is BookUiState.Success && element.book.id == curBookId.value) {
                val oldPage = element.book.pages[newPage.number - 1]
                val newPageCopy = newPage.copy(
                    styles = newPage.styles.toList()
                )
                val added = newPageCopy.styles.filterNot { oldPage.styles.contains(it) }
                val deleted = oldPage.styles.filterNot { newPageCopy.styles.contains(it) }
                val newBook: Book = element.book.copy(
                    pages = element.book.pages.toMutableList().apply {
                        set(newPage.number - 1, newPageCopy)
                    }
                )
                updatePageUseCase(newPageCopy.id, added, deleted).launchIn(viewModelScope)
                element.copy(book = newBook)
            } else element
        }
    }
    fun changeLastNum(newLastNum: Int){
        booksUiState.value = booksUiState.value.map {element ->
            if (element is BookUiState.Success && element.book.id == curBookId.value) {
                val newBook = element.book.copy(lastPage = newLastNum)
                updateBookUseCase(newBook).launchIn(viewModelScope)
                element.copy(book = newBook)
            }
            else element
        }
    }

    fun addNote(pageNum: Int, text: String, onResult: (Long) -> Unit) {
        val curBook = booksUiState.value.find { element ->
            (element is BookUiState.Success && element.book.id == curBookId.value)}
        if (curBook is BookUiState.Success){
            viewModelScope.launch {
                val noteId = addNoteUseCase(Note(0, curBook.book.id, pageNum, text)).first()
                onResult(noteId)
            }
        }
    }

    fun register(user: User, onResult: (Boolean) -> Unit){
        booksUiState.value = listOf()
        viewModelScope.launch {
            addUserUseCase(user).collect{id ->
                if(id != -1L) {
                    viewModelScope.launch {
                        dataStore.edit { account ->
                            account[PreferencesKeys.USER_ID] = id
                        }
                    }
                    onResult(true)
                }
                else
                    onResult(false)
            }
        }
    }

    fun login(email: String, password: String, onResult: (String) -> Unit){
        viewModelScope.launch {
            val foundUser = getUserUseCase(email).first()
            if (foundUser == null){
                onResult("Email")
                return@launch
            }
            if (!BCrypt.checkpw(password, foundUser.hashedPassword)){
                onResult("Password")
                return@launch
            }
            viewModelScope.launch {
                dataStore.edit { account ->
                    account[PreferencesKeys.USER_ID] = foundUser.id
                }
            }
            booksUiState.value = listOf()
            onResult("")
        }
    }
}