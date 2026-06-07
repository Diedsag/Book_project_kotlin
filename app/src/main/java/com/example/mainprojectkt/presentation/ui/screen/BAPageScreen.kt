package com.example.mainprojectkt.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.contextmenu.builder.item
import androidx.compose.foundation.text.contextmenu.modifier.appendTextContextMenuComponents
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.domain.model.Note
import com.example.mainprojectkt.domain.model.PageWithStyles
import com.example.mainprojectkt.domain.model.StyleRange
import com.example.mainprojectkt.presentation.ui.component.ColorChoicer
import com.example.mainprojectkt.presentation.ui.component.NoteChoicer
import com.example.mainprojectkt.presentation.ui.component.buildPage

@Composable
fun BAPageScreen(
    nPages: Int,
    page: PageWithStyles,
    curColor: Color,
    allNotes: List<Note>,
    bookNotes: List<Note>,
    onMove: (Int) -> Unit,
    onChangeStyle: (PageWithStyles) -> Unit,
    onAddNote: (Long, String, (Long) -> Unit) -> Unit,
    onHome: () -> Unit,
    onDeleteNote: (Long) -> Unit,
    onChangeColor: (Color) -> Unit,
    onBack: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {true})
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            when (dismissState.currentValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (page.number > 1)
                        onMove(page.number - 1)
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    if (page.number < nPages)
                        onMove(page.number + 1)
                }
                else -> {}
            }
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }
    LaunchedEffect(page.number) {
        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
    }
    val focusManager = LocalFocusManager.current
    BackHandler {
        focusManager.clearFocus()
        onBack()
    }
    var showNoteState by remember { mutableIntStateOf(0) }
    var showColorChoicer by remember { mutableStateOf(false) }
    var colorChosen by remember { mutableStateOf(curColor) }
    var noteText by remember { mutableStateOf("")}
    var curFilter by remember { mutableIntStateOf(0) }
    var noteChosen: Note? by remember { mutableStateOf(null) }

    // Выбираем список заметок в зависимости от фильтра
    val filteredNotes = when (curFilter) {
        0 -> allNotes      // "Все"
        1 -> bookNotes  // "Текущей книги"
        else -> emptyList()
    }
    var selection by remember(page.number) { mutableStateOf(TextRange(0, 0)) }

    val annotatedString = remember(page.id, page.text, page.styles.hashCode()) {
        buildPage(page.text, page.styles)
    }
    val safeSelection = if (selection.end <= annotatedString.length) selection else TextRange(0, 0)

    val textFieldValue = remember(annotatedString, safeSelection)
    {TextFieldValue(annotatedString, safeSelection) }

    val textStyle = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily.Default,
        textAlign = TextAlign.Start,
        lineHeight = 20.sp
    )

    SwipeToDismissBox(dismissState, {}) {
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
                            Icon(Icons.Default.Home, "На главную")
                        }
                        Row(
                            modifier = Modifier.weight(0.7f),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ) {
                            Text(page.number.toString())
                        }
                        Button({ showColorChoicer = true }, modifier = Modifier.weight(0.3f)) {
                            Icon(Icons.Default.Palette, "Выбрать цвет")
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                if (showNoteState == 1) {
                    AlertDialog(
                        onDismissRequest = { showNoteState = 0 },
                        title = { Text("Заметка") },
                        text = {
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = { noteText = it },
                                label = { Text("Текст") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                maxLines = Int.MAX_VALUE,
                                singleLine = false,
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                onAddNote(page.id, noteText) { id ->
                                    val newStyles = addSticker(page.styles, safeSelection, curColor, "myapp://note/$id")
                                    onChangeStyle(PageWithStyles(page.id, page.number, page.text, newStyles, page.images))
                                    noteText = ""
                                    showNoteState = 0
                                }
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { noteText = ""; showNoteState = 0 }) { Text("Отмена") }
                        }
                    )
                }
                if (showNoteState == 2) {
                    AlertDialog(
                        onDismissRequest = { showNoteState = 0 },
                        title = { Text("Заметки") },
                        text = {
                            NoteChoicer(
                                elements = filteredNotes,
                                curFilter = curFilter,
                                onFilterChange = { newFilter ->
                                    curFilter = newFilter
                                },
                                sendNote = {note ->
                                    noteChosen = note
                                }
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                showNoteState = 0
                                noteChosen?.let {
                                    val newStyles = addSticker(
                                        page.styles,
                                        safeSelection,
                                        curColor,
                                        "myapp://note/${it.id}"
                                    )
                                    onChangeStyle(
                                        PageWithStyles(
                                            page.id,
                                            page.number,
                                            page.text,
                                            newStyles,
                                            page.images
                                        )
                                    )
                                    noteChosen = null
                                }
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { noteText = ""; showNoteState = 0 }) { Text("Отмена") }
                        }
                    )
                }

                if (showColorChoicer) {
                    AlertDialog(
                        onDismissRequest = { showColorChoicer = false },
                        title = { Text("Цвет") },
                        text = { ColorChoicer(curColor) { colorChosen = it } },
                        confirmButton = {
                            Button(onClick = {
                                onChangeColor(colorChosen)
                                showColorChoicer = false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showColorChoicer = false }) { Text("Отмена") }
                        }
                    )
                }

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (newValue.selection != safeSelection) {
                            selection = newValue.selection
                        }
                    },
                    textStyle = textStyle.copy(color = Color.Transparent),
                    readOnly = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .appendTextContextMenuComponents {
                            separator()
                            item(key = "ShowColor", label = "Изменить цвет") {
                                showColorChoicer = true
                                close()
                            }
                            if (safeSelection.end != safeSelection.start) {
                                separator()
                                item(key = "AddSticker", label = "Добавить стикер") {
                                    if (curColor != Color.Transparent) {
                                        val newStyles =
                                            addSticker(page.styles, safeSelection, curColor, null)
                                        onChangeStyle(
                                            PageWithStyles(
                                                page.id,
                                                page.number,
                                                page.text,
                                                newStyles,
                                                page.images
                                            )
                                        )
                                    }
                                    close()
                                }
                                separator()
                                item(key = "DeleteSticker", label = "Убрать стикер") {
                                    val newStyles = cutSticker(page.styles, safeSelection)
                                    onChangeStyle(
                                        PageWithStyles(
                                            page.id,
                                            page.number,
                                            page.text,
                                            newStyles,
                                            page.images
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
                                    val newStyles = deleteNote(page.styles, safeSelection) { id ->
                                        onDeleteNote(id)
                                    }
                                    onChangeStyle(
                                        PageWithStyles(
                                            page.id,
                                            page.number,
                                            page.text,
                                            newStyles,
                                            page.images
                                        )
                                    )
                                    close()
                                }
                            }
                        }
                )

                Text(
                    text = annotatedString,
                    style = textStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

fun addSticker(
    styles: List<StyleRange>,
    selection: TextRange,
    color: Color,
    link: String?
): List<StyleRange> {
    if (selection.start == selection.end) return styles

    val newStyle = StyleRange(selection, SpanStyle(background = color), link)
    return (styles + newStyle).sortedBy { it.link != null }
}

fun cutSticker(styles: List<StyleRange>, selection: TextRange): List<StyleRange> {
    val newStyles = mutableListOf<StyleRange>()
    for (style in styles) {
        val start = style.textRange.start
        val end = style.textRange.end
        val tStart = selection.start
        val tEnd = selection.end
        if (end <= tStart || start >= tEnd || style.link != null) {
            newStyles.add(style)
        } else {
            if (start < tStart) newStyles.add(style.copy(textRange = TextRange(start, tStart)))
            if (end > tEnd) newStyles.add(style.copy(textRange = TextRange(tEnd, end)))
        }
    }
    return newStyles
}
fun deleteNote(
    styles: List<StyleRange>,
    selection: TextRange,
    onDelete: (Long) -> Unit
): List<StyleRange> {
    val newStyles = mutableListOf<StyleRange>()
    for (style in styles) {
        if (style.link != null && selection.intersects(style.textRange)) {
            val id = style.link.removeSurrounding("myapp://note/", "").toLongOrNull()
            if (id != null) onDelete(id)
        } else {
            newStyles.add(style)
        }
    }
    return newStyles
}