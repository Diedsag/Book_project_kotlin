package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UploadBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val scanBookUseCase: ScanBookUseCase,
    val uploadBookUseCase: UploadBookUseCase,
    val downloadBooksUseCase: DownloadBooksUseCase
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
    var last_id: Long = 0
    var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    var curBookId = MutableStateFlow<Long?>(null)
    init{
        viewModelScope.launch {
            downloadBooksUseCase().collect {elements ->
                booksUiState.value += elements.map {
                    element ->
                    last_id = element.id
                    BookUiState.Success(element)
                }
                hasBook.value = true
            }
        }
    }
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        scanBook()
    }
    fun scanBook(){
        documentUri.value?.let{
            viewModelScope.launch {
                val new_id = ++last_id //Change for case of different users
                booksUiState.value += BookUiState.Loading(new_id)
                scanBookUseCase(it).collect {element ->
                    booksUiState.value = booksUiState.value.toMutableList().apply{
                        set(booksUiState.value.size - 1, BookUiState.Success(element.copy(id = new_id)))
                    }
                }
                curBookId.value = new_id
                hasBook.value = true
            }
        }

    }
    fun changePage(newPage: PageWithStyles){
        booksUiState.value = booksUiState.value.map {element ->
            if (element is BookUiState.Success && element.book.id == curBookId.value) {
                element.copy(book = element.book.copy(
                    pages = element.book.pages.toMutableList().apply {
                        set(newPage.number - 1, newPage)
                    }
                    )
                )
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