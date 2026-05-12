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
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.usecase.GetPagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BAViewModel (
    val context: Context,
    val getPagesUseCase: GetPagesUseCase
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
    var pagesState: MutableStateFlow<List<PageWithStyles>> = MutableStateFlow(listOf<PageWithStyles>())
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
        getPages()
    }
    fun getPages(){
        documentUri.value?.let{
            viewModelScope.launch {
                getPagesUseCase(it).collect { element->
                    pagesState.value += element
                }
            }
        }
    }
    fun changePages(new_page: PageWithStyles){
        pagesState.value = pagesState.value.toMutableList().apply {
            set(new_page.number - 1, new_page)
        }
    }
}