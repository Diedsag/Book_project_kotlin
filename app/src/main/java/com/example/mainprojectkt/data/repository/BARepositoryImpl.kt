package com.example.mainprojectkt.data.repository

import android.net.Uri
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.repository.BARepository
import kotlinx.coroutines.flow.Flow

class BARepositoryImpl(
    val dataSource: BADataSource
): BARepository{
    override fun getPages(uri: Uri): Flow<PageWithStyles> {
        return dataSource.getPages(uri)
    }
}