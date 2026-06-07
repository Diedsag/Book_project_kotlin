package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
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
import com.example.mainprojectkt.domain.usecase.DeleteBookUseCase
import com.example.mainprojectkt.domain.usecase.DeleteNoteUseCase
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetNotesUseCase
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
import kotlinx.coroutines.flow.update
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
    val getNotesUseCase: GetNotesUseCase,
    val deleteNoteUseCase: DeleteNoteUseCase,
    val addUserUseCase: AddUserUseCase,
    val getUserUseCase: GetUserUseCase,
    val deleteBookUseCase: DeleteBookUseCase
) : ViewModel(
) {
    val tempIdCounter = AtomicLong(0L)
    var userId: MutableStateFlow<Long> = MutableStateFlow(-1L)
    var userState: MutableStateFlow<User?> = MutableStateFlow(null)
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    var notesState: MutableStateFlow<List<Note>> = MutableStateFlow(listOf())
    var colorState: MutableStateFlow<Color> = MutableStateFlow(Color.Green)
    var curBookId = MutableStateFlow<Long?>(null)
    private val dataStore = (context.applicationContext as BookApplication).dataStore
    init{
        viewModelScope.launch {
            userId.flatMapLatest { id ->
                getUserUseCase(id)
            }.collect { userState.value = it }
        }

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
            getNotesUseCase().collect{
                notesState.value = it
            }
        }
    }
    fun scanBook(uri: Uri?) {
        if (userId.value == -1L) return
        uri?.let { uri ->
            val newId = tempIdCounter.decrementAndGet()
            booksUiState.value += BookUiState.Loading(newId)
            viewModelScope.launch(Dispatchers.IO) {
                val savedId = scanBookUseCase(uri, userId.value)
                booksUiState.value = booksUiState.value.map { state ->
                    if (state is BookUiState.Loading && state.id == newId) {
                        BookUiState.Loading(savedId)
                    }
                    else state
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

    fun addNote(pageId: Long, text: String, onResult: (Long) -> Unit) {
        val curBook = booksUiState.value.find { element ->
            (element is BookUiState.Success && element.book.id == curBookId.value)}
        if (curBook is BookUiState.Success){
            viewModelScope.launch {
                val noteId = addNoteUseCase(Note(0, pageId, text)).first()
                onResult(noteId)
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id).collect {}
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
            if (foundUser.id == userId.value){
                onResult("Second")
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

    fun changeColor(color: Color){
        colorState.value = color
    }

    fun deleteBook(book: Book){
        val found = booksUiState.value.find { (it is BookUiState.Success && it.book == book) }
        found?.let { booksUiState.value -= it
            viewModelScope.launch {
                deleteBookUseCase(book).collect {}
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            dataStore.edit { account ->
                account[PreferencesKeys.USER_ID] = -1L
            }
        }
        booksUiState.value = listOf()
    }
}