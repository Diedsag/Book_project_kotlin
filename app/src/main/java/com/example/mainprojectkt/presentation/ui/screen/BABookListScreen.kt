package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.presentation.ui.component.BookDialog
import com.example.mainprojectkt.presentation.ui.component.SwipeToRevealCard
import com.example.mainprojectkt.presentation.viewmodel.BookUiState


@Composable
fun BABookListScreen(
    books: List<BookUiState>,
    onSelect: (Long) -> Unit,
    onResume: (Long) -> Unit,
    onBack: () -> Unit,
    onDelete: (Book) -> Unit,
    onUpdate: (Book) -> Unit,
    onAdd: () -> Unit
) {
    var bookToEdit by remember { mutableStateOf<Book?>(null) }
    Scaffold(
        bottomBar = {
            Column {
                Button(onClick = { onBack() }) {
                    Icon(
                        Icons.Default.Home,
                        "На главную"
                    )
                    Text(" На главную")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Список книг",
                    fontSize = 50.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
                IconButton(onClick = { onAdd() }) {
                    Icon(
                        Icons.Default.Add,
                        "Добавить",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            if (books.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("У вас пока нет книг", fontSize = 18.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
                    items(
                        books,
                        key = { state ->
                            when (state) {
                                is BookUiState.Success -> state.book.id
                                is BookUiState.Loading -> state.id
                            }
                        }
                    ) { item ->
                        when (item) {
                            is BookUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is BookUiState.Success -> {
                                SwipeToRevealCard(
                                    book = item.book,
                                    onEdit = { book ->
                                        bookToEdit = book
                                    },
                                    onDelete = onDelete
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp, horizontal = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .clickable { onResume(item.book.id) }
                                                    .padding(16.dp)
                                            ) {
                                                Text(
                                                    item.book.name,
                                                    fontSize = 20.sp
                                                )
                                                Text(
                                                    item.book.author,
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    "Страниц: ${item.book.pages.size}",
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(end = 16.dp)
                                                    .align(Alignment.CenterVertically)
                                                    .clickable { onSelect(item.book.id) },
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Default.Bookmarks,
                                                    "Подробнее",
                                                    modifier = Modifier.size(32.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Text("Подробнее", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    bookToEdit?.let { book ->
        BookDialog(
            currentTitle = book.name,
            onDismissRequest = {
                bookToEdit = null
            },
            onConfirm = { newName ->
                onUpdate(book.copy(name = newName))
                bookToEdit = null
            },
            false
        )
    }
}