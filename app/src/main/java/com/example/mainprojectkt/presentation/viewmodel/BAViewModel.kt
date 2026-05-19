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
   var booksUiState: MutableStateFlow<List<BookUiState>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    var curBookId = MutableStateFlow<Int?>(null)
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        scanBook()
    }
    fun scanBook(){
        documentUri.value?.let{
            viewModelScope.launch {
                val new_id = booksUiState.value.size - 1 //Change for case of different users
                booksUiState.value += BookUiState.Loading(new_id)
                scanBookUseCase(it).collect {element ->
                    booksUiState.value = booksUiState.value.toMutableList().apply{
                        set(booksUiState.value.size - 1, BookUiState.Success(element.copy(id = new_id)))
                    }
                    curBookId.value = element.id
                }
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
        book.let { it ->
            uploadBookUseCase((it as BookUiState.Success).book).launchIn(viewModelScope)
        }
    }
    fun getCount(){
        viewModelScope.launch {
            downloadBooksUseCase().collect { data ->
                Log.d("TAG", data.size.toString())
            }
        }
    }
}