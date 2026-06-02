package com.example.mainprojectkt.data.local

import android.content.Context
import android.net.Uri
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class BookInfo(
    val title: String?,
    val author: String?
)

data class ShiftSizes(
    val leftMargin: Float = 50f,
    val rightMargin: Float = 50f,
    val topMargin: Float = 100f,
    val bottomMargin: Float = 50f
)
class BADataSource(
    private val context: Context,
    private val maxConcurrency: Int = Runtime.getRuntime().availableProcessors()
) {
    suspend fun getBookInfo(uri: Uri): BookInfo = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PdfReader(stream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val info = pdfDoc.documentInfo
                    BookInfo(
                        title = info.title,
                        author = info.getMoreInfo("Author")
                    )
                }
            }
        } ?: BookInfo(null, null)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPages(uri: Uri): List<PageWithStyles> = flow {
        val pdfReader = PdfReader(context.contentResolver.openInputStream(uri))
        val pdfDoc = PdfDocument(pdfReader)
        val pageCount = pdfDoc.numberOfPages
        val shiftSizes = ShiftSizes()
        val strategy = LocationTextExtractionStrategy()
        (1..pageCount).asFlow()
            .flatMapMerge(concurrency = maxConcurrency) { pageNum ->
                flow {
                    emit(scanPage(uri, pageNum, shiftSizes, strategy))
                }.flowOn(Dispatchers.Default)
            }
            .collect { page ->
                emit(page)
            }
    }.flowOn(Dispatchers.IO).toList()

    private fun scanPage(
        uri: Uri,
        pageNum: Int,
        shiftSizes: ShiftSizes,
        strategy: LocationTextExtractionStrategy
    ): PageWithStyles {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return PageWithStyles(pageNum, "", emptyList())

        inputStream.use { inputStream ->
            PdfReader(inputStream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val page = pdfDoc.getPage(pageNum)
                    val pageSize = page.pageSize

                    val rect = Rectangle(
                        pageSize.left + shiftSizes.leftMargin,
                        pageSize.bottom + shiftSizes.bottomMargin,
                        pageSize.right - shiftSizes.rightMargin,
                        pageSize.top - shiftSizes.topMargin
                    )

                    val filter = TextRegionEventFilter(rect)
                    val listener = FilteredTextEventListener(strategy, filter)
                    val pageText = PdfTextExtractor.getTextFromPage(page, listener)
                    return PageWithStyles(pageNum, pageText, listOf())
                }
            }
        }
    }
}