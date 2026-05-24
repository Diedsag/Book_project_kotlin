package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class UpdatePageUseCase(private val repository: BARepository) {
    operator fun invoke(bookId: Long, page: PageWithStyles): Flow<Unit> = repository.updatePage(bookId, page)
}