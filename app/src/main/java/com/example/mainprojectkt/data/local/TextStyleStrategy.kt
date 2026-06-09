package com.example.mainprojectkt.data.local

import android.graphics.Color
import androidx.compose.ui.text.TextRange
import com.example.mainprojectkt.domain.model.TextStyleData
import com.example.mainprojectkt.domain.model.StyleRange
import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.canvas.parser.EventType
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy
import kotlin.math.abs

class TextStyleStrategy : ITextExtractionStrategy {
    private val textBuilder = StringBuilder()
    private val styleRanges = mutableListOf<StyleRange>()
    private var currentChunk: TextChunk? = null
    private var lastBaselineY: Float? = null
    private var lastEndX: Float? = null
    private var lastFontSize: Float = 12f
    private val fontSizeSamples = mutableListOf<Float>()
    private var dominantFontSize: Float? = null

    override fun getResultantText(): String {
        flushChunk()
        return textBuilder.toString()
    }

    fun getStyleRanges(): List<StyleRange> {
        flushChunk()
        return styleRanges
    }

    override fun eventOccurred(data: IEventData, type: EventType) {
        if (type != EventType.RENDER_TEXT) return

        val renderInfo = data as TextRenderInfo
        val text = renderInfo.text
        if (text.isNullOrEmpty()) return

        val baseline = renderInfo.baseline
        val startPoint = baseline.getStartPoint() ?: return
        val endPoint = baseline.getEndPoint() ?: startPoint

        val currentBaselineY = startPoint.get(1)
        val currentStartX = startPoint.get(0)
        val currentEndX = endPoint.get(0)

        val font = renderInfo.font
        val fontSize = renderInfo.fontSize
        val fontName = font.fontProgram?.fontNames?.fontName ?: "Default"

        fontSizeSamples.add(fontSize)
        if (fontSizeSamples.size >= 20 && dominantFontSize == null) {
            dominantFontSize = fontSizeSamples
                .groupingBy { it.toInt() }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key?.toFloat()
        }

        val isBold = fontName.lowercase().contains("bold") || fontName.lowercase().contains("black")
        val isItalic = fontName.lowercase().contains("italic") || fontName.lowercase().contains("oblique")

        val colorArgb: Int = when (val fillColor = renderInfo.fillColor) {
            is DeviceRgb -> {
                val rgb = fillColor.getColorValue()
                Color.argb(
                    255,
                    (rgb[0] * 255).toInt().coerceIn(0, 255),
                    (rgb[1] * 255).toInt().coerceIn(0, 255),
                    (rgb[2] * 255).toInt().coerceIn(0, 255)
                )
            }
            is DeviceGray -> {
                val g = (fillColor.getColorValue()[0] * 255).toInt().coerceIn(0, 255)
                Color.argb(255, g, g, g)
            }
            else -> Color.BLACK
        }

        val indexType = determineIndexType(
            currentFontSize = fontSize,
            currentBaselineY = currentBaselineY,
            currentStartX = currentStartX
        )

        lastBaselineY?.let { prevY ->
            val yDiff = prevY - currentBaselineY
            if (indexType == "normal" && abs(yDiff) > lastFontSize * 0.5f) {
                flushChunk()
                textBuilder.append('\n')
                lastEndX = null
            } else if (indexType == "normal") {
                lastEndX?.let { prevEndX ->
                    val horizontalGap = currentStartX - prevEndX
                    val spaceThreshold = lastFontSize * 0.25f
                    if (horizontalGap > spaceThreshold &&
                        textBuilder.isNotEmpty() &&
                        !textBuilder.endsWith(' ') &&
                        !textBuilder.endsWith('\n')) {
                        val spaceStyle = TextStyleData(
                            size = fontSize, font = fontName,
                            bold = isBold, italic = isItalic, color = colorArgb
                        )
                        val spaceChunk = TextChunk(" ", spaceStyle)
                        if (currentChunk != null && currentChunk!!.canMerge(spaceChunk)) {
                            currentChunk!!.text += " "
                        } else {
                            flushChunk()
                            currentChunk = spaceChunk
                        }
                    }
                }
            }
        }

        if (indexType == "normal") {
            lastBaselineY = currentBaselineY
        }
        lastFontSize = fontSize
        lastEndX = currentEndX

        val textStyle = TextStyleData(
            size = fontSize,
            index = indexType,
            font = fontName,
            bold = isBold,
            italic = isItalic,
            color = colorArgb
        )
        val newChunk = TextChunk(text, textStyle)

        if (currentChunk == null) {
            currentChunk = newChunk
        } else if (currentChunk!!.canMerge(newChunk)) {
            currentChunk!!.text += newChunk.text
        } else {
            flushChunk()
            currentChunk = newChunk
        }
    }

    private fun determineIndexType(
        currentFontSize: Float,
        currentBaselineY: Float,
        currentStartX: Float
    ): String {
        val prevY = lastBaselineY ?: return "normal"
        val prevEndX = lastEndX ?: return "normal"

        val refSize = dominantFontSize ?: lastFontSize

        val isSmallEnough = currentFontSize <= 11f && currentFontSize <= refSize * 0.85f
        if (!isSmallEnough) return "normal"

        val yDiff = abs(currentBaselineY - prevY)
        val onSameLine = yDiff <= refSize * 0.7f
        if (!onSameLine) return "normal"

        val horizontalGap = currentStartX - prevEndX
        val immediatelyAfter = horizontalGap <= refSize * 0.3f
        if (!immediatelyAfter) return "normal"

        val baselineShift = currentBaselineY - prevY
        return when {
            baselineShift > refSize * 0.15f -> "superscript"
            baselineShift < -refSize * 0.10f -> "subscript"
            else -> "normal"
        }
    }

    private fun flushChunk() {
        currentChunk?.let { chunk ->
            if (chunk.text.isEmpty()) return@let
            val start = textBuilder.length
            textBuilder.append(chunk.text)
            val end = textBuilder.length
            styleRanges.add(
                StyleRange(
                    textRange = TextRange(start, end),
                    originalStyle = chunk.textStyle,
                    link = null
                )
            )
        }
        currentChunk = null
    }

    override fun getSupportedEvents(): Set<EventType> = setOf(EventType.RENDER_TEXT)

    private data class TextChunk(
        var text: String,
        val textStyle: TextStyleData
    ) {
        fun canMerge(other: TextChunk): Boolean = this.textStyle == other.textStyle
    }
}