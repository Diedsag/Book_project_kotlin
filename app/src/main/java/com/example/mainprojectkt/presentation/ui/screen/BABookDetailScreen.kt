package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.presentation.viewmodel.BookUiState


@Composable
fun BABookDetailScreen(
    book: Book,
    onMove: (Int) -> Unit,
    onBack: () -> Unit,
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
                book.name ?: "Название не указано",
                fontSize = 50.sp,
                lineHeight = 1.2.em,
                modifier = Modifier.padding(bottom = 30.dp)
            )
            LazyColumn {
                items(
                    book.pages,
                    key = {it.number}
                ) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onMove(item.number) }) {
                            Text(
                                item.number.toString(),
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                            item.styles.sortedBy { it.textRange.start }.forEach {
                                Box(
                                    modifier = Modifier
                                        .width(5.dp)
                                        .border(1.dp, Color.Black)
                                        .height(40.dp)
                                        .background(it.spanStyle.background )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)
                    .clickable { onMove(book.lastPage) },) {
                Icon(
                    Icons.Default.PlayArrow,
                    "Возобновить чтение"
                )
            }
        }
    }
}