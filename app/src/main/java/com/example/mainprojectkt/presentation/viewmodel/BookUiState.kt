package com.example.mainprojectkt.presentation.viewmodel

import com.example.mainprojectkt.domain.model.Book

sealed class BookUiState {
    data class Loading(val id: Int): BookUiState()
    data class Success(val book: Book) : BookUiState()
}