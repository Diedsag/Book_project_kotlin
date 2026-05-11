package com.example.mainprojectkt.domain.repository

import android.net.Uri
import com.example.mainprojectkt.domain.model.PageWithStyles
import kotlinx.coroutines.flow.Flow

interface BARepository {
    fun getText(number: Int): String
    fun getTextUri(uri: Uri, number : Int): String
    fun getPages(uri: Uri): Flow<PageWithStyles>
}