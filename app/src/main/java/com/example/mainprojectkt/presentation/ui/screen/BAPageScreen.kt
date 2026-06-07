package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.presentation.ui.component.ColorChoicer
import com.example.mainprojectkt.presentation.viewmodel.BookUiState

@Composable
fun BAPageScreen(
    nPages: Int,
    page: PageWithStyles,
    curColor: Color,
    notes: List<Note>,
    onMove: (Int) -> Unit,
    onChangeStyle: (PageWithStyles) -> Unit,
    onAddNote: (Long, String, (Long) -> Unit) -> Unit,
    onHome: () -> Unit,
    onDeleteNote: (Long) -> Unit,
    onChangeColor: (Color) -> Unit
){
    BackHandler(enabled = true){
        if (page.number > 1)
            onMove(page.number - 1)
    }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd && page.number > 1){
                onMove(page.number - 1)
                true
            } else {
                if (dismissValue == SwipeToDismissBoxValue.EndToStart && page.number < nPages){
                    onMove(page.number + 1)
                    true
                }
                else
                    false
            }
        }
    )
    var showNoteState by remember { mutableIntStateOf(0) }
    var showColorChoicer by remember { mutableStateOf(false) }
    var colorChosen by remember { mutableStateOf(curColor) }
    var noteText by remember { mutableStateOf("") }
    val styleRanges by remember { mutableStateOf(page.styles.toMutableList()) }
    var value by remember {mutableStateOf(TextFieldValue(page.text))}
    val images = page.images
    value = addSticker(value, page.styles.toMutableList(), null, null)

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
                Column {
                    Slider(
                        value = page.number.toFloat(),
                        onValueChange = { onMove(it.toInt()) },
                        valueRange = 1f..nPages.toFloat(),
                        steps = nPages - 1
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        Arrangement.Start,
                        Alignment.CenterVertically
                    ) {
                        Button({ onHome() }, modifier = Modifier.weight(0.3f)) {
                            Icon(
                                Icons.Default.Home,
                                "На главную"
                            )
                        }
                        Row(
                            modifier = Modifier.weight(0.7f),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ){
                            Text(page.number.toString())
                        }
                        Button({showColorChoicer = true}, modifier = Modifier.weight(0.3f)) {
                            Icon(
                                Icons.Default.Palette,
                                "Выбрать цвет"
                            )
                        }
                    }
                }
            }
        ) {paddingValues ->
            Box(modifier = Modifier.wrapContentSize().padding(paddingValues).verticalScroll(rememberScrollState())) {
                if (showNoteState == 1) {
                    AlertDialog(
                        onDismissRequest = { showNoteState = 0 },
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
                                    onAddNote(page.id, noteText){
                                        id -> value = addSticker(value, styleRanges, curColor, "myapp://note/$id")
                                        onChangeStyle(PageWithStyles(page.id, page.number, value.text, styleRanges, images))
                                        noteText = ""
                                        showNoteState = 0
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
                                    showNoteState = 0
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

                if (showNoteState == 2) {
                    AlertDialog(
                        onDismissRequest = { showNoteState = 0 },
                        title = { Text("Заметки") },
                        text = {
                            /*LazyColumn {
                                items(
                                    notes,
                                    key = {it.id}
                                ) { note ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp, horizontal = 12.dp)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(0.8f)
                                                    .clickable { onSelect(item.book.id) }) {
                                                Text(
                                                    note.pageId,
                                                    fontSize = 30.sp
                                                )
                                                Text(
                                                    "Страниц: " + item.book.pages.size.toString(),
                                                    fontSize = 15.sp
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.fillMaxWidth(0.5f).align(Alignment.CenterVertically)
                                                    .clickable { onResume(item.book.id) },) {
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    "Возобновить чтение"
                                                )
                                            }
                                        }
                                    }
                                }
                            }*/
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onAddNote(page.id, noteText){id ->
                                        value = addSticker(value, styleRanges, curColor, "myapp://note/$id")
                                        onChangeStyle(PageWithStyles(page.id, page.number, value.text, styleRanges, images))
                                        showNoteState = 0
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
                                    showNoteState = 0
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

                if (showColorChoicer) {
                    AlertDialog(
                        onDismissRequest = { showColorChoicer = false },
                        title = { Text("Цвет") },
                        text = {
                            Column {
                                ColorChoicer(curColor){
                                    colorChosen = it
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onChangeColor(colorChosen)
                                    showColorChoicer = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showColorChoicer = false
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
                            item(key = "ShowColor", label = "Изменить цвет") {
                                showColorChoicer = true
                                close()
                            }
                            if (value.selection.end != value.selection.start) {
                                separator()
                                item(key = "AddSticker", label = "Добавить стикер") {
                                    if (curColor != Color.Transparent) {
                                        value = addSticker(value, styleRanges, curColor, null)
                                        onChangeStyle(
                                            PageWithStyles(
                                                page.id,
                                                page.number,
                                                value.text,
                                                styleRanges,
                                                images
                                            )
                                        )
                                    }
                                    close()
                                }
                                separator()
                                item(key = "DeleteSticker", label = "Убрать стикер") {
                                    value = cutSticker(value, styleRanges)
                                    onChangeStyle(
                                        PageWithStyles(
                                            page.id,
                                            page.number,
                                            value.text,
                                            styleRanges,
                                            images
                                        )
                                    )
                                    close()
                                }
                                separator()
                                item(key = "AddNote", label = "Добавить заметку") {
                                    showNoteState = 1
                                    close()
                                }
                                separator()
                                item(key = "ChooseNote", label = "Выбрать заметку") {
                                    showNoteState = 2
                                    close()
                                }
                                separator()
                                item(key = "DeleteNote", label = "Убрать заметку") {
                                    deleteNote(value, styleRanges) { id -> onDeleteNote(id) }
                                    onChangeStyle(
                                        PageWithStyles(
                                            page.id,
                                            page.number,
                                            value.text,
                                            styleRanges,
                                            images
                                        )
                                    )
                                    close()
                                }
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

fun addSticker(value: TextFieldValue,
                styleRanges: MutableList<StyleRange>,
                color: Color?,
                link: String?): TextFieldValue{
    color?.let{
        val newStyleRange = StyleRange(
            TextRange(
                value.selection.start,
                value.selection.end),
            SpanStyle(background = it),
            link
        )
        styleRanges.add(newStyleRange)
    }
    val sortedRanges = styleRanges.sortedBy { it.link != null }
    val newString = buildAnnotatedString {
        append(value.text)
        sortedRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle.copy(
                    textDecoration = if (styleRange.link != null) TextDecoration.Underline
                    else TextDecoration.None),
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

fun cutSticker(value: TextFieldValue,
               styleRanges: MutableList<StyleRange>): TextFieldValue{
    val textRange = value.selection
    val newStyleRanges = mutableListOf<StyleRange>()
    for (style in styleRanges) {
        val start = style.textRange.start
        val end = style.textRange.end
        val tStart = textRange.start
        val tEnd = textRange.end
        if (end <= tStart || start >= tEnd || style.link != null)
            newStyleRanges.add(style)
        else {
            if (start < tStart) {
                newStyleRanges.add(style.copy(textRange = TextRange(start, tStart)))
            }
            if (end > tEnd) {
                newStyleRanges.add(style.copy(textRange = TextRange(tEnd, end)))
            }
        }
    }
    styleRanges.clear()
    styleRanges.addAll(newStyleRanges)
    val newString = buildAnnotatedString {
        append(value.text)
        styleRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle.copy(
                    textDecoration = if (styleRange.link != null) TextDecoration.Underline
                    else TextDecoration.None),
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


fun deleteNote(value: TextFieldValue,
               styleRanges: MutableList<StyleRange>,
               onDelete: (Long) -> Unit): TextFieldValue{
    val textRange = value.selection
    val iterator = styleRanges.iterator()
    while (iterator.hasNext()) {
        val styleRange = iterator.next()
        if (styleRange.link != null){
            if (textRange.intersects(styleRange.textRange)){
                val id = styleRange.link.removeSurrounding(prefix = "myapp://note/", suffix = "").toLong()
                onDelete(id)
                iterator.remove()
            }
        }
    }
    val newString = buildAnnotatedString {
        append(value.text)
        styleRanges.forEach { styleRange ->
            addStyle(
                styleRange.spanStyle.copy(
                    textDecoration = if (styleRange.link != null) TextDecoration.Underline
                    else TextDecoration.None),
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