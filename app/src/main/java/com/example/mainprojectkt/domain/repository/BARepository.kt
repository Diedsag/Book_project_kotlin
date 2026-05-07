package com.example.mainprojectkt.domain.repository

import android.net.Uri

interface BARepository {
    fun getText(number: Int): String
    fun getTextUri(uri: Uri, number : Int): String
}