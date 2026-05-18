package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val scanBookUseCase: ScanBookUseCase,
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
    var pagesState: MutableStateFlow<List<PageWithStyles>> = MutableStateFlow(listOf<PageWithStyles>())
    var booksState: MutableStateFlow<List<Book>> = MutableStateFlow(listOf())
    val hasBook = MutableStateFlow(false)
    var curBook = MutableStateFlow(0)
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        scanBook()
    }
    fun scanBook(){
        documentUri.value?.let{
            viewModelScope.launch {
                scanBookUseCase(it).collect {element ->
                    booksState.value += element.copy(id = booksState.value.size)
                    curBook.value = booksState.value.size - 1
                }
                hasBook.value = true
            }
        }
    }
    fun changePage(newPage: PageWithStyles){
        booksState.value = booksState.value.map {element ->
            if (element.id == curBook.value) {
                element.copy(pages =
                    element.pages.toMutableList().apply {
                        set(newPage.number - 1, newPage)
                    }
                )
            }
            else element
        }
    }
    fun changeLastNum(newLastNum: Int){
        booksState.value = booksState.value.map {element ->
            if (element.id == curBook.value) {
                element.copy(lastPage = newLastNum)
            }
            else element
        }
    }
}