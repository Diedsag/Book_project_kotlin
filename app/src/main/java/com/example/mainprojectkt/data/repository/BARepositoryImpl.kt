package com.example.mainprojectkt.data.repository

import android.net.Uri
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.domain.repository.BARepository

class BARepositoryImpl(
    val dataSource: BADataSource
): BARepository{
    override fun getText(number: Int): String {
        return dataSource.getText(number)
    }
    override fun getTextUri(uri: Uri, number: Int): String {
        return dataSource.getTextUri(uri, number)
    }
}