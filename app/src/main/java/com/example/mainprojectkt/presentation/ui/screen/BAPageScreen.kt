package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.itextpdf.text.pdf.TextField

@Composable
fun BAPageScreen(
    n_pages: Int,
    page: PageWithStyles,
    onMove: (Int) -> Unit,
    onChangeStyle: (PageWithStyles) -> Unit,
    onBack: () -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd){
                onMove(page.number - 1)
                true
            } else {
                if (dismissValue == SwipeToDismissBoxValue.EndToStart){
                    onMove(page.number + 1)
                    true
                }
                else
                    false
            }
        }
    )
    val styleRanges by remember { mutableStateOf(page.styles.toMutableList()) }
    val sliderPosition by remember { mutableFloatStateOf(page.number.toFloat()) }
    var value by remember {mutableStateOf(TextFieldValue(page.text))}
    value = changeValue(value, page.styles.toMutableList(), null)

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
        Scaffold(
            bottomBar = {
                Column() {
                    Slider(
                        value = sliderPosition,
                        onValueChange = { onMove(it.toInt()) },
                        valueRange = 1f..n_pages.toFloat(),
                        steps = n_pages - 2
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {
                        Button({ onBack() }) {
                            Icon(
                                Icons.Default.Home,
                                "На главную"
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ){
                            Text(page.number.toString())
                        }
                    }
                }
            }
        ) {paddingValues ->
            Box(modifier = Modifier.wrapContentSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                Text(
                    value.annotatedString,
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .appendTextContextMenuComponents {
                            separator()
                            item(key = "Blue", label = "Синий") {
                                value = changeValue(value, styleRanges, Color.Blue)
                                onChangeStyle(PageWithStyles(page.number, value.text, styleRanges))
                                close()
                            }
                            separator()
                            item(key = "Yellow", label = "Желтый") {
                                value = changeValue(value, styleRanges, Color.Yellow)
                                onChangeStyle(PageWithStyles(page.number,value.text, styleRanges))
                                close()
                            }
                        }
                )
            }

        }
    }
}

data class StyleRange(
    val textRange: TextRange,
    val spanStyle: SpanStyle,
    val link: String?
)
fun changeValue(value: TextFieldValue, styleRanges: MutableList<StyleRange>, color: Color?) : TextFieldValue{
    color?.let{
        val newStyleRange = StyleRange(
            TextRange(
                value.selection.start,
                value.selection.end),
            SpanStyle(background = it),
            null
        )
        if (styleRanges.contains(newStyleRange))
            styleRanges.remove(newStyleRange)
        else
            styleRanges.add(newStyleRange)
    }
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