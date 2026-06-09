package com.example.mainprojectkt.presentation.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.R
import com.example.mainprojectkt.domain.model.Note

@Composable
fun FilterDropdown(
    curFilter: Int,
    onFilterChange: (Int) -> Unit
) {
    val filters = listOf(stringResource(R.string.filter_all), stringResource(R.string.filter_current_book))
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(filters[curFilter])
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filters.forEachIndexed { index, filterName ->
                DropdownMenuItem(
                    text = { Text(filterName) },
                    onClick = {
                        onFilterChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun NoteChoicer(
    elements: List<Note>,
    curFilter: Int,
    onFilterChange: (Int) -> Unit,
    sendNote: (Note) -> Unit
) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    Column {
        FilterDropdown(
            curFilter = curFilter,
            onFilterChange = onFilterChange
        )

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .height(300.dp)
                .weight(1f, fill = false)
        ) {
            items(
                items = elements,
                key = { it.id }
            ) { note ->
                val isSelected = note.id == selectedNote?.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedNote = note
                            sendNote(note)
                        }
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                )
                            } else {
                                Modifier
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.text.take(20) + if (note.text.length > 20) "..." else "",
                            fontSize = 14.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            if (elements.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.info_no_notes),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}