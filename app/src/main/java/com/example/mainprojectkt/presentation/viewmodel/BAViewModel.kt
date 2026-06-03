package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.AddNoteUseCase
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetNoteUseCase
import com.example.mainprojectkt.domain.usecase.GetUserBooksUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdateBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdatePageUseCase
import com.example.mainprojectkt.domain.usecase.UploadBookUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

class BAViewModel (
    val context: Context,
    val scanBookUseCase: ScanBookUseCase,
    val uploadBookUseCase: UploadBookUseCase,
    val downloadBooksUseCase: DownloadBooksUseCase,
    val updateBookUseCase: UpdateBookUseCase,
    val updatePageUseCase: UpdatePageUseCase,
    val getUserBooksUseCase: GetUserBooksUseCase,
    val addNoteUseCase: AddNoteUseCase,
    val getNoteUseCase: GetNoteUseCase
) : ViewModel(
) {
    var lastId = AtomicLong(0)
    val userId: Long = 1
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    var notesState: MutableStateFlow<List<Note>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    var curBookId = MutableStateFlow<Long?>(null)
    init{
        viewModelScope.launch {
            downloadBooksUseCase().collect { elements ->
                booksUiState.value = booksUiState.value.map { bookUiState ->
                    if (bookUiState is BookUiState.Loading && bookUiState.id in elements.map { it.id }) {
                        lastId.set(bookUiState.id)
                        return@map BookUiState.Success(elements.find { it.id == bookUiState.id }!!)
                    }
                    return@map bookUiState
                }

                
            }
        }
        viewModelScope.launch {
            getNoteUseCase().collect{
               notesState.value = it
            }
        }
        viewModelScope.launch {
            getUserBooksUseCase(userId).collect { it.forEach { id ->
                if (booksUiState.value.find { bookUiState ->
                        (bookUiState is BookUiState.Loading && bookUiState.id == id)
                            || (bookUiState is BookUiState.Success && bookUiState.book.id == id)} == null) {
                        booksUiState.value += BookUiState.Loading(id)
                    }
                }
                
            }
        }
    }
    fun scanBook(uri: Uri?) {
        uri?.let { uri ->
            val newId = lastId.incrementAndGet()
            booksUiState.value += BookUiState.Loading(newId)
            viewModelScope.launch(Dispatchers.IO) {
                scanBookUseCase(uri)
            }
        }
    }
    fun changePage(newPage: PageWithStyles){
        booksUiState.value = booksUiState.value.map {element ->
            if (element is BookUiState.Success && element.book.id == curBookId.value) {
                val newBook: Book = element.book.copy(
                    pages = element.book.pages.toMutableList().apply {
                        set(newPage.number - 1, newPage)
                    }
                )
                updatePageUseCase(curBookId.value!!, newPage).launchIn(viewModelScope)
                element.copy(book = newBook)
            }
            else element
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
}