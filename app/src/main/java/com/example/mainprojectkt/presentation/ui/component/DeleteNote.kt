package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.ui.text.TextRange
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.domain.model.TextStyleData

fun deleteNote(
    styles: List<StyleRange>,
    selection: TextRange,
    onDelete: (Long) -> Unit
): List<StyleRange> {
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
            if (coveringStyle.link != null) {
                val id = coveringStyle.link.removePrefix("myapp://note/").toLongOrNull()
                if (id != null) onDelete(id)
            }

            val hasBackground = coveringStyle.originalStyle?.backgroundColor != null
            val hasOriginalStyle = coveringStyle.originalStyle != null

            if (hasBackground || (hasOriginalStyle && coveringStyle.originalStyle != TextStyleData())) {
                result.add(
                    StyleRange(
                        textRange = TextRange(segStart, segEnd),
                        originalStyle = coveringStyle.originalStyle,
                        link = null
                    )
                )
            }
        }
    }

    return result.sortedBy { it.link != null }
}