package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.presentation.viewmodel.BookUiState

@Composable
fun BABookListScreen(
    books: List<BookUiState>,
    onSelect: (Long) -> Unit,
    onResume: (Long) -> Unit,
    onBack: () -> Unit,
    curBookId: Long?
) {
    Scaffold(
        bottomBar = {
            Column() {
                Button({onBack()}) {
                    Icon(
                        Icons.Default.Home,
                        "На главную"
                    )
                }
            }
        }
    ) {padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                "Список книг",
                fontSize = 50.sp,
                modifier = Modifier.padding(bottom = 30.dp)
            )
            if (books.isEmpty())
                Text("У вас пока нет книг")
            else{
                LazyColumn {
                    items(
                        books,
                        key = {state ->
                            when (state) {
                                is BookUiState.Loading -> state.id
                                is BookUiState.Success -> state.book.id
                            }
                        }
                    ) { item ->
                        when(item){
                            is BookUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is BookUiState.Success -> {
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
                                                item.book.name ?: "Название не указано",
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
                        }
                    }
                }
            }
        }
    }
}