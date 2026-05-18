package com.example.mainprojectkt.data.repository

import android.net.Uri
import androidx.room.Database
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class BARepositoryImpl(
    val dataSource: BADataSource,
    val database: BADatabase
): BARepository{
    override fun scanBook(uri: Uri): Flow<Book> {
        return dataSource.scanBook(uri)
    }
}