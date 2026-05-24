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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    var last_id: Long = 0
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    var curBookId = MutableStateFlow<Long?>(null)
    init{
        viewModelScope.launch {
            downloadBooksUseCase().first().forEach {element ->
                last_id = element.id
                booksUiState.value += BookUiState.Success(element)
            }
        }
        viewModelScope.launch {
            downloadBooksUseCase().collect {elements ->
                elements.forEach { Log.d("TAG", "el_id:" + it.id.toString()) }
                booksUiState.value.forEach { if(it is BookUiState.Loading) Log.d("TAG", "bs_id:" + it.id.toString()) }
                booksUiState.value = booksUiState.value.map { bookUiState ->
                    if(bookUiState is BookUiState.Loading && bookUiState.id in elements.map { it.id }) {
                        Log.d("TAG", "loading changed: " + bookUiState.id.toString())
                        return@map BookUiState.Success(elements.find{ it.id == bookUiState.id }!!)
                    }
                    return@map bookUiState
                }
            }
        }
    }
    fun scanBook(uri: Uri?){
        uri?.let {
            booksUiState.value += BookUiState.Loading(++last_id)
            viewModelScope.launch {
                scanBookUseCase(it, last_id).onSuccess { id ->
                    Log.d("TAG", "id:$id")
                }
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
                element.copy(book = element.book.copy(
                    lastPage = newLastNum)
                )
            }
            else element
        }
    }
    fun uploadBook(book: BookUiState?){
        if (book != null)
            uploadBookUseCase((book as BookUiState.Success).book).launchIn(viewModelScope)
    }
    fun getCount(){
        viewModelScope.launch {
            downloadBooksUseCase().collect { data ->
                Log.d("TAG", data.size.toString())
            }
        }
    }
    fun checkRelation(id: Int){
        viewModelScope.launch {
            downloadBooksUseCase(id.toLong()).collect {x ->
                Log.d("TAG", "bm: " + x!!.book.name)
                Log.d("TAG", "ps: " + x.pages.size.toString())
            }
        }
    }
}