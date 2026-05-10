package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itextpdf.text.pdf.TextField

@Composable
fun BAPageScreen(
    number: Int,
    text: String,
    onMove: (Int) -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd ) {
                onMove(-1)
                true
            } else {
                if (dismissValue == SwipeToDismissBoxValue.EndToStart){
                    onMove(1)
                    true
                }
                else
                    false
            }
        }
    )
    val styleRanges by remember { mutableStateOf(mutableListOf<StyleRange>()) }
    var value by remember {mutableStateOf(TextFieldValue(text))}

    val textStyle = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily.Default,
        textAlign = TextAlign.Start,
        lineHeight = 20.sp
    )
    SwipeToDismissBox(
        dismissState,
        {}
    ) {
            Box {
                Text(value.annotatedString,
                    style = textStyle.copy(color = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle = textStyle,
                    readOnly = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth().appendTextContextMenuComponents {
                        separator()
                        item(key = "Blue", label = "Синий") {
                            value = changeValue(value, styleRanges, Color.Blue)
                            close()
                        }
                        separator()
                        item(key = "Yellow", label = "Желтый") {
                            value = changeValue(value, styleRanges, Color.Yellow)
                            close()
                        }
                    }
                )
            }
            Text(number.toString())
        }

}

data class StyleRange(
    val textRange: TextRange,
    val spanStyle: SpanStyle,
    val link: String?
)
fun changeValue(value: TextFieldValue, styleRanges: MutableList<StyleRange>, color: Color) : TextFieldValue{
    val newStyleRange = StyleRange(
        TextRange(
            value.selection.start,
            value.selection.end),
        SpanStyle(background = color),
        null
    )
    if (styleRanges.contains(newStyleRange))
        styleRanges.remove(newStyleRange)
    else
        styleRanges.add(newStyleRange)
    val new_string = buildAnnotatedString {
        append(value.text)
        styleRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle,
                styleRange.textRange.start,
                styleRange.textRange.end
            )
            styleRange.link?.let{
                addLink(
                    LinkAnnotation.Url(
                        it),
                    styleRange.textRange.start,
                    styleRange.textRange.end
                )
            }
        }
    }
    return TextFieldValue(new_string, value.selection)
}