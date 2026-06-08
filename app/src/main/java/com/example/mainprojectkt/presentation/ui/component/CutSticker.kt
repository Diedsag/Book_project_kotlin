package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.ui.text.TextRange
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.domain.model.TextStyleData

fun cutSticker(styles: List<StyleRange>, selection: TextRange): List<StyleRange> {
    if (selection.start == selection.end) return styles

    val tStart = selection.start
    val tEnd = selection.end

    val points = mutableSetOf(tStart, tEnd)
    for (style in styles) {
        if (style.textRange.start in (tStart + 1)..<tEnd) {
            points.add(style.textRange.start)
        }
        if (style.textRange.end in (tStart + 1)..<tEnd) {
            points.add(style.textRange.end)
        }
    }
    val sortedPoints = points.sorted()

    val result = mutableListOf<StyleRange>()

    for (style in styles) {
        if (style.textRange.end <= tStart || style.textRange.start >= tEnd) {
            result.add(style)
        } else {
            if (style.textRange.start < tStart) {
                result.add(style.copy(textRange = TextRange(style.textRange.start, tStart)))
            }
            if (style.textRange.end > tEnd) {
                result.add(style.copy(textRange = TextRange(tEnd, style.textRange.end)))
            }
        }
    }
    for (i in 0 until sortedPoints.size - 1) {
        val segStart = sortedPoints[i]
        val segEnd = sortedPoints[i + 1]
        if (segStart < tStart || segEnd > tEnd) continue
        if (segStart == segEnd) continue

        val mid = (segStart + segEnd) / 2
        val coveringStyle = styles.find {
            it.textRange.start <= mid && it.textRange.end > mid
        }

        if (coveringStyle != null) {
            val baseStyle = coveringStyle.originalStyle ?: TextStyleData()
            val updatedStyle = baseStyle.copy(backgroundColor = null)

            val isDefault = updatedStyle == TextStyleData()
            val hasLink = coveringStyle.link != null

            if (!isDefault || hasLink) {
                result.add(
                    StyleRange(
                        textRange = TextRange(segStart, segEnd),
                        originalStyle = if (isDefault) null else updatedStyle,
                        link = coveringStyle.link
                    )
                )
            }
        }
    }

    return result.sortedBy { it.link != null }
}