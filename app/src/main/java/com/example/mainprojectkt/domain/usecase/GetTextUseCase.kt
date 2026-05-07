package com.example.mainprojectkt.domain.usecase

import android.net.Uri
import com.example.mainprojectkt.domain.repository.BARepository

class GetTextUseCase(private val repository: BARepository) {
    operator fun invoke(uri: Uri, number: Int) = repository.getTextUri(uri, number)
}