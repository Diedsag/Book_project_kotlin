package com.example.mainprojectkt.domain.usecase

import android.net.Uri
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class GetPagesUseCase(private val repository: BARepository) {
    operator fun invoke(uri: Uri): Flow<PageWithStyles> = repository.getPages(uri)
}