package com.example.mainprojectkt.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.itextpdf.text.pdf.PdfDocument
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.net.URI

class BADataSource(val context: Context) {
    fun scanBook(uri: Uri): Flow<Book> = flow{
        val pdfReader = PdfReader(context.contentResolver.openInputStream(uri))
        val pages = mutableListOf<PageWithStyles>()
        for (i in 1..pdfReader.numberOfPages)
                pages.add(
                PageWithStyles(
                    i, PdfTextExtractor.getTextFromPage(pdfReader, i), listOf()
                    )
                )
        emit(Book(-1,pdfReader.info.get("Title"), pages.toList(), 1))
    }.flowOn(Dispatchers.IO)
}