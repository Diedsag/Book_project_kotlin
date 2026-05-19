package com.example.mainprojectkt.presentation.viewmodel

import com.example.mainprojectkt.domain.model.Book

sealed class BookUiState {
    data class Loading(val id: Long): BookUiState()
    data class Success(val book: Book) : BookUiState()
}