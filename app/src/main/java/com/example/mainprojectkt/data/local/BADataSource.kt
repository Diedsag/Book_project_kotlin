package com.example.mainprojectkt.data.local

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.mainprojectkt.data.model.BookDto
import com.example.mainprojectkt.data.model.ShiftSizes
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.io.File


class BADataSource(
    private val context: Context,
    private val maxConcurrency: Int = Runtime.getRuntime().availableProcessors()
) {
    fun getFileNameFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        return when (uri.scheme) {
            "file" -> File(uri.path).name
            "content" -> {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        cursor.getString(nameIndex)
                    } else null
                }
            }
            else -> null
        }
    }
    suspend fun getBookInfo(uri: Uri): BookDto = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            PdfReader(stream).use { reader ->
                PdfDocument(reader).use { pdfDoc ->
                    val info = pdfDoc.documentInfo
                    val fileName = getFileNameFromUri(uri, context.contentResolver)
                    BookDto(
                        title = info.title ?: fileName?.removeSuffix(".pdf"),
                        author = info.getMoreInfo("Author")
                    )
                }
            }
        } ?: BookDto(null, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getPages(uri: Uri, styled: Boolean): List<PageWithStyles> = flow {
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
                    emit(scanPage(uri, pageNum, styled))
                }.flowOn(Dispatchers.Default)
            }.collect {page ->
                page?.let{emit(page)}
            }
    }.flowOn(Dispatchers.IO).toList()

    private fun scanPage(uri: Uri, pageNum: Int, styled: Boolean): PageWithStyles? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
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
                    val filter = TextRegionEventFilter(rect)

                    val pageText: String
                    val styles: List<StyleRange>

                    if (styled) {
                        val strategy = TextStyleStrategy()
                        val listener = FilteredTextEventListener(strategy, filter)
                        PdfTextExtractor.getTextFromPage(page, listener)
                        pageText = strategy.resultantText
                        styles = strategy.getStyleRanges()
                    } else {
                        val strategy = LocationTextExtractionStrategy()
                        val listener = FilteredTextEventListener(strategy, filter)
                        pageText = PdfTextExtractor.getTextFromPage(page, listener)
                        styles = listOf()
                    }
                    return PageWithStyles(0, pageNum, pageText, styles, listOf())
                }
            }
        }
    }
}