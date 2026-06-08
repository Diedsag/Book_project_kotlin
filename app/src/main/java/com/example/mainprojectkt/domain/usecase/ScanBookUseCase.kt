package com.example.mainprojectkt.domain.usecase

import android.net.Uri
import com.example.mainprojectkt.domain.repository.BARepository

class ScanBookUseCase(private val repository: BARepository) {
    suspend operator fun invoke(uri: Uri, styled: Boolean, userId: Long): Long = repository.scanBook(uri, styled, userId)
}