package com.example.mainprojectkt.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.mainprojectkt.data.model.BookDto
import com.example.mainprojectkt.data.model.ShiftSizes
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class BADataSource(
    private val context: Context,
    private val maxConcurrency: Int = Runtime.getRuntime().availableProcessors()
) {
    suspend fun getBookInfo(uri: Uri): BookDto = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PdfReader(stream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val info = pdfDoc.documentInfo
                    BookDto(
                        title = info.title,
                        author = info.getMoreInfo("Author")
                    )
                }
            }
        } ?: BookDto(null, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPages(uri: Uri): List<PageWithStyles> = flow {
        val pageCount = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                PdfReader(stream).use { reader ->
                    PdfDocument(reader).use { pdfDoc ->
                        pdfDoc.numberOfPages
                    }
                }
            } ?: 0
        }
        (1..pageCount).asFlow()
            .flatMapMerge(concurrency = maxConcurrency) { pageNum ->
                flow {
                    emit(scanPage(uri, pageNum))
                }.flowOn(Dispatchers.Default)
            }.collect {page ->
                page?.let{emit(page)}
            }
    }.flowOn(Dispatchers.IO).toList()

    private fun scanPage(uri: Uri, pageNum: Int): PageWithStyles? {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return null
        inputStream.use { stream ->
            PdfReader(stream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val page = pdfDoc.getPage(pageNum)
                    val pageSize = page.pageSize
                    val shiftSizes = ShiftSizes()
                    val rect = Rectangle(
                        pageSize.left + shiftSizes.leftMargin,
                        pageSize.bottom + shiftSizes.bottomMargin,
                        pageSize.right - shiftSizes.rightMargin,
                        pageSize.top - shiftSizes.topMargin
                    )
                    val strategy = LocationTextExtractionStrategy()
                    val filter = TextRegionEventFilter(rect)
                    val listener = FilteredTextEventListener(strategy, filter)
                    val pageText = PdfTextExtractor.getTextFromPage(page, listener)
                    return PageWithStyles(pageNum, pageText, listOf())
                }
            }
        }
    }
}