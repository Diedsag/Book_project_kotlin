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
import com.example.mainprojectkt.domain.usecase.GetBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val getBookUseCase: GetBookUseCase,
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
    var pagesState: MutableStateFlow<List<PageWithStyles>> = MutableStateFlow(listOf<PageWithStyles>())
    var bookState: MutableStateFlow<Book> = MutableStateFlow(Book("", listOf(), 1))
    val hasBook = MutableStateFlow(false)
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        getPages()
    }
    fun getPages(){
        documentUri.value?.let{
            viewModelScope.launch {
                getBookUseCase(it).collect {
                    bookState.value = it }
                pagesState.value = bookState.value.pages
                hasBook.value = true
            }
        }
    }
//    fun getBook(){
//        documentUri.value?.let{
//            viewModelScope.launch {
//                getBookUseCase(it).collect { element->
//                    bookState.value.pages += element
//                }
//                getting = true
//            }
//        }
//    }
    fun changeBook(newPage: PageWithStyles){
        bookState.value = Book(
            bookState.value.name,
            bookState.value.pages.toMutableList().apply {
                set(newPage.number - 1, newPage)
            },
            bookState.value.lastPage)
    }
    fun changeLastNum(newLastNum: Int){
        bookState.value = Book(
            bookState.value.name,
            bookState.value.pages,
            newLastNum)
    }
}