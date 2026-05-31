package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdateBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdatePageUseCase
import com.example.mainprojectkt.domain.usecase.UploadBookUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val scanBookUseCase: ScanBookUseCase,
    val uploadBookUseCase: UploadBookUseCase,
    val downloadBooksUseCase: DownloadBooksUseCase,
    val updateBookUseCase: UpdateBookUseCase,
    val updatePageUseCase: UpdatePageUseCase,
) : ViewModel(
) {
    var lastId: Long = 0
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    val asyncDownload = viewModelScope.async {
        downloadBooksUseCase().collect { elements ->
            booksUiState.value = booksUiState.value.map { bookUiState ->
                if (bookUiState is BookUiState.Loading && bookUiState.id in elements.map { it.id }) {
                    return@map BookUiState.Success(elements.find { it.id == bookUiState.id }!!)
                }
                return@map bookUiState
            }
        }
    }


    var curBookId = MutableStateFlow<Long?>(null)
    init{
        viewModelScope.launch {
            downloadBooksUseCase().first().forEach {element ->
                lastId = element.id
                booksUiState.value += BookUiState.Success(element)
            }
        }
        /*viewModelScope.launch {
            downloadBooksUseCase().collect {elements ->
                booksUiState.value = booksUiState.value.map { bookUiState ->
                    if(bookUiState is BookUiState.Loading && bookUiState.id in elements.map { it.id }) {
                        return@map BookUiState.Success(elements.find{ it.id == bookUiState.id }!!)
                    }
                    return@map bookUiState
                }
            }
        }*/
    }
    fun scanBook(uri: Uri?){
        uri?.let { uri ->
            booksUiState.value += BookUiState.Loading(++lastId)
            val asyncScan = viewModelScope.async{scanBookUseCase(uri)}
            viewModelScope.launch {
                asyncScan.await()
            }
            viewModelScope.launch {
                asyncDownload.await()
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
                viewModelScope.launch {
                    updateBookUseCase(newBook).collect {  }
                }
                element.copy(book = newBook)
            }
            else element
        }
    }
}