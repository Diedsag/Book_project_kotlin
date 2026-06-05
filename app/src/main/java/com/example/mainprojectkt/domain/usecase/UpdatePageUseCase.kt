package com.example.mainprojectkt.domain.usecase

import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class UpdatePageUseCase(private val repository: BARepository) {
    operator fun invoke(bookId: Long, added: List<StyleRange>, deleted: List<StyleRange>): Flow<Unit> = repository.updatePage(bookId, added, deleted)
}