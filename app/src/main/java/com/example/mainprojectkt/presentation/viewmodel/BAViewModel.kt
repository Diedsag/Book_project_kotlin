package com.example.mainprojectkt.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.mainprojectkt.domain.usecase.GetTextUseCase

class BAViewModel (
    val context: Context,
    val getTextUseCase: GetTextUseCase
) : ViewModel(
) {
    fun getText(number: Int): String{
        return getTextUseCase(number)
    }
}