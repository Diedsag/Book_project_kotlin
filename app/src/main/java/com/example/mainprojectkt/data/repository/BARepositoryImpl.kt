package com.example.mainprojectkt.data.repository

import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.domain.repository.BARepository

class BARepositoryImpl(
    val dataSource: BADataSource
): BARepository{
    override fun getText(number: Int): String {
        return dataSource.getText(number)
    }
}