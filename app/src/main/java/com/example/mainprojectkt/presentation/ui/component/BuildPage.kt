package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.example.mainprojectkt.domain.model.StyleRange

fun buildPage(text: String, styles: List<StyleRange>): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        val sortedRanges = styles.sortedBy { it.link != null }
        sortedRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle.copy(
                    textDecoration = if (styleRange.link != null) TextDecoration.Underline
                    else TextDecoration.None
                ),
                styleRange.textRange.start,
                styleRange.textRange.end
            )
            styleRange.link?.let {
                addLink(
                    LinkAnnotation.Url(it),
                    styleRange.textRange.start,
                    styleRange.textRange.end
                )
            }
        }
    }
}