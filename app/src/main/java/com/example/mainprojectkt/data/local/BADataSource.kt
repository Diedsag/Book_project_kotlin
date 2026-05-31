package com.example.mainprojectkt.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BADataSource(val context: Context) {
    fun scanBook(uri: Uri): Flow<Book> = flow{
        val pdfReader = PdfReader(context.contentResolver.openInputStream(uri))
        val pdfDoc = PdfDocument(pdfReader)
        val pages = mutableListOf<PageWithStyles>()

        val leftMargin = 50f
        val rightMargin = 50f
        val topMargin = 100f
        val bottomMargin = 50f

        var pageSize: Rectangle
        var rect: Rectangle
        var filter: TextRegionEventFilter
        var extractionStrategy: LocationTextExtractionStrategy
        var listener: FilteredTextEventListener

        for (i in 1..pdfDoc.numberOfPages) {
            pageSize = pdfDoc.getPage(i).pageSize
            rect = Rectangle(
                pageSize.left + leftMargin,
                pageSize.bottom + bottomMargin,
                pageSize.right - rightMargin,
                pageSize.top - topMargin
            )
            filter = TextRegionEventFilter(rect)
            extractionStrategy = LocationTextExtractionStrategy()
            listener = FilteredTextEventListener(extractionStrategy, filter)
            pages.add(
                PageWithStyles(
                    i, PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i), listener), listOf()
                )
            )
        }
        emit(Book(0, pdfDoc.documentInfo.title, pages.toList(), 1))

    }.flowOn(Dispatchers.IO)
}