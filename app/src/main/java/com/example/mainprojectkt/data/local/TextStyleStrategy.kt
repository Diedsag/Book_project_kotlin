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

class TextStyleStrategy : ITextExtractionStrategy {
    val textBuilder = StringBuilder()
    private val styleRanges = mutableListOf<StyleRange>()
    private var currentChunk: TextChunk? = null
    var lastBaselineY: Float? = null
    var lastEndX: Float? = null
    var lastFontSize: Float = 12f
    var normalBaselineY: Float? = null

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
        val startPoint = baseline.startPoint
        val endPoint = baseline.endPoint

        val currentBaselineY = startPoint?.get(1) ?: return
        val currentStartX = startPoint.get(0)
        val currentEndX = endPoint?.get(0) ?: currentStartX

        val font = renderInfo.font
        val fontSize = renderInfo.fontSize
        val fontName = font.fontProgram?.fontNames?.fontName ?: "Default"

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

        val indexType: String
        val isIndex = fontSize < lastFontSize * 0.9f

        if (isIndex && normalBaselineY != null) {
            val baselineDiff = currentBaselineY - normalBaselineY!!
            val threshold = lastFontSize * 0.2f
            indexType = when {
                baselineDiff > threshold -> "superscript"
                baselineDiff < -threshold -> "subscript"
                else -> "normal"
            }
        } else {
            indexType = "normal"
            normalBaselineY = currentBaselineY
        }
        lastBaselineY?.let { prevY ->
            val yDiff = prevY - currentBaselineY
            val effectiveYDiff = if (indexType == "superscript" || indexType == "subscript") {
                (normalBaselineY ?: prevY) - (normalBaselineY ?: prevY)
            } else {
                yDiff
            }

            if (indexType == "normal" && effectiveYDiff > lastFontSize * 0.5f) {
                flushChunk()
                textBuilder.append('\n')
                lastBaselineY = currentBaselineY
                lastEndX = null
                normalBaselineY = currentBaselineY
            } else if (indexType == "normal" && effectiveYDiff < -lastFontSize * 0.5f) {
                flushChunk()
                textBuilder.append('\n')
                lastBaselineY = currentBaselineY
                lastEndX = null
                normalBaselineY = currentBaselineY
            } else {
                lastEndX?.let { prevEndX ->
                    val horizontalGap = currentStartX - prevEndX
                    val spaceThreshold = lastFontSize * 0.25f
                    if (horizontalGap > spaceThreshold && textBuilder.isNotEmpty() && !textBuilder.endsWith(' ') && !textBuilder.endsWith('\n')) {
                        val spaceChunk = TextChunk(" ",
                            TextStyleData(fontSize, "normal", fontName, isBold, isItalic, colorArgb))
                        if (currentChunk != null && currentChunk!!.canMerge(spaceChunk)) {
                            currentChunk!!.text += " "
                        } else {
                            flushChunk()
                            currentChunk = spaceChunk
                        }
                    }
                }
                if (indexType == "normal") {
                    lastBaselineY = currentBaselineY
                }
            }
        } ?: run {
            lastBaselineY = currentBaselineY
            normalBaselineY = currentBaselineY
        }

        lastFontSize = fontSize
        lastEndX = currentEndX

        val textStyle = TextStyleData(fontSize, indexType, fontName, isBold, isItalic, colorArgb)
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