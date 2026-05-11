package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.GetPagesUseCase
import com.example.mainprojectkt.domain.usecase.GetTextUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val getPagesUseCase: GetPagesUseCase
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
    var todosState: MutableStateFlow<List<PageWithStyles>> = MutableStateFlow(listOf<PageWithStyles>())
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        getPages()
    }
    fun getPages(){
        documentUri.value?.let{
            viewModelScope.launch {
                getPagesUseCase(it).collect { element->
                    todosState.value += element
                }
            }
        }
    }
    fun changePages(new_pages: List<PageWithStyles>){
        todosState.value = new_pages
    }
}