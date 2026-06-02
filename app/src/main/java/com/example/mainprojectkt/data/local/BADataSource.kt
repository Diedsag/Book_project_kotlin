package com.example.mainprojectkt.data.local

import android.content.Context
import android.net.Uri
import android.util.Log
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
        // без изменений (работает корректно)
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
        // 1. Быстро получаем количество страниц (отдельный короткий вызов)
        val pageCount = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                PdfReader(stream).use { reader ->
                    PdfDocument(reader).use { pdfDoc ->
                        pdfDoc.numberOfPages
                    }
                }
            } ?: 0
        }

        // 2. Обрабатываем страницы параллельно, НО с уникальной стратегией на каждую страницу
        (1..pageCount).asFlow()
            .flatMapMerge(concurrency = maxConcurrency) { pageNum ->
                flow {
                    emit(scanPage(uri, pageNum))
                }.flowOn(Dispatchers.Default)
            }
            .collect { page ->
                emit(page)
            }
    }.flowOn(Dispatchers.IO).toList()

    private fun scanPage(uri: Uri, pageNum: Int): PageWithStyles {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return PageWithStyles(pageNum, "", emptyList())

        inputStream.use { stream ->
            PdfReader(stream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val page = pdfDoc.getPage(pageNum)
                    val pageSize = page.pageSize

                    // Отступы — можно вынести в параметры или сделать настраиваемыми
                    val shiftSizes = ShiftSizes(
                        leftMargin = 50f,
                        rightMargin = 50f,
                        topMargin = 100f,
                        bottomMargin = 50f
                    )
                    val rect = Rectangle(
                        pageSize.left + shiftSizes.leftMargin,
                        pageSize.bottom + shiftSizes.bottomMargin,
                        pageSize.right - shiftSizes.rightMargin,
                        pageSize.top - shiftSizes.topMargin
                    )

                    // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: новая стратегия для каждой страницы
                    val strategy = LocationTextExtractionStrategy()
                    val filter = TextRegionEventFilter(rect)
                    val listener = FilteredTextEventListener(strategy, filter)
                    val pageText = PdfTextExtractor.getTextFromPage(page, listener)

                    Log.d("TAG!", "Page $pageNum extracted, length = ${pageText.length}")
                    return PageWithStyles(pageNum, pageText, listOf())
                }
            }
        }
    }
}