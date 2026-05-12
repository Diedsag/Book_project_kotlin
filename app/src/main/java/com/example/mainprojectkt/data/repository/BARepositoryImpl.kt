package com.example.mainprojectkt.data.repository

import android.net.Uri
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class BARepositoryImpl(
    val dataSource: BADataSource
): BARepository{
    override fun getBook(uri: Uri): Flow<Book> {
        return dataSource.getBook(uri)
    }
}