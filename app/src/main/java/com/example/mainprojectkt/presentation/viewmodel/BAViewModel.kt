package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mainprojectkt.domain.usecase.GetTextUseCase

class BAViewModel (
    val context: Context,
    val getTextUseCase: GetTextUseCase
) : ViewModel(
) {
    var documentUri = mutableStateOf<Uri?>(null)
//    fun getText(number: Int): String{
//        return getTextUseCase(number)
//    }
    fun getText(number: Int): String{
        return getTextUseCase(documentUri.value!!, number)
    }
    fun changeUri(newUri: Uri){
        documentUri.value = newUri
    }
}