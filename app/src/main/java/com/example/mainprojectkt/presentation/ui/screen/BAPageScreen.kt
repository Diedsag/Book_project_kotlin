package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
@Composable
fun BAPageScreen(
    n_pages: Int,
    page: PageWithStyles,
    onMove: (Int) -> Unit,
    onChangeStyle: (PageWithStyles) -> Unit,
    onAddNote: (Int, String, (Long) -> Unit) -> Unit,
    onBack: () -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd && page.number > 1){
                onMove(page.number - 1)
                true
            } else {
                if (dismissValue == SwipeToDismissBoxValue.EndToStart && page.number < n_pages){
                    onMove(page.number + 1)
                    true
                }
                else
                    false
            }
        }
    )
    var showNoteRedactor by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    val styleRanges by remember { mutableStateOf(page.styles.toMutableList()) }
    val sliderPosition by remember { mutableFloatStateOf(page.number.toFloat()) }
    var value by remember {mutableStateOf(TextFieldValue(page.text))}
    value = changeValue(value, page.styles.toMutableList(), null, null)

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
                        steps = n_pages - 1
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
                if (showNoteRedactor) {
                    AlertDialog(
                        onDismissRequest = { showNoteRedactor = false },
                        title = { Text("Заметка") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    label = { Text("Текст") },
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    maxLines = Int.MAX_VALUE,
                                    singleLine = false,
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onAddNote(page.number, noteText){
                                        id -> value = changeValue(value, styleRanges,
                                        Color.Green, "myapp://note/$id")
                                        onChangeStyle(PageWithStyles(page.number, value.text, styleRanges))
                                        showNoteRedactor = false
                                    }
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    noteText = ""
                                    showNoteRedactor = false
                                }
                            ) {
                                Text("Отмена")
                            }
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        )
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    textStyle = textStyle.copy(color = Color.Transparent),
                    readOnly = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .appendTextContextMenuComponents {
                            separator()
                            item(key = "Blue", label = "Синий") {
                                value = changeValue(value, styleRanges, Color.Blue, null)
                                onChangeStyle(PageWithStyles(page.number, value.text, styleRanges))
                                close()
                            }
                            separator()
                            item(key = "Yellow", label = "Желтый") {
                                value = changeValue(value, styleRanges, Color.Yellow, null)
                                onChangeStyle(PageWithStyles(page.number,value.text, styleRanges))
                                close()
                            }
                            separator()
                            item(key = "Note", label = "Добавить заметку") {
                                showNoteRedactor = true
                                close()
                            }
                        }
                )
                Text(
                    value.annotatedString,
                    style = textStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

        }
    }
}

fun changeValue(value: TextFieldValue,
                styleRanges: MutableList<StyleRange>,
                color: Color?,
                link: String?) : TextFieldValue{
    color?.let{
        val newStyleRange = StyleRange(
            TextRange(
                value.selection.start,
                value.selection.end),
            SpanStyle(background = it),
            link
        )
        if (styleRanges.contains(newStyleRange))
            styleRanges.remove(newStyleRange)
        else
            styleRanges.add(newStyleRange)
    }
    val newString = buildAnnotatedString {
        append(value.text)
        styleRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle,
                styleRange.textRange.start,
                styleRange.textRange.end
            )
            styleRange.link?.let{
                addLink(
                    LinkAnnotation.Url(styleRange.link),
                    styleRange.textRange.start,
                    styleRange.textRange.end
                )
            }
        }
    }
    return TextFieldValue(newString, value.selection)
}