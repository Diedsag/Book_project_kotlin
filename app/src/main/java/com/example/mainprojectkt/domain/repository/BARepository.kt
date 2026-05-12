package com.example.mainprojectkt.domain.repository

import android.net.Uri
import com.example.mainprojectkt.domain.model.PageWithStyles
import kotlinx.coroutines.flow.Flow

interface BARepository {
    fun getPages(uri: Uri): Flow<PageWithStyles>
}