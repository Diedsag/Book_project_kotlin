package com.example.mainprojectkt.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
    onItemClick: (Long) -> Unit,
    curBookId: Long?
) {
    Scaffold(
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
                books.forEach {
                        it ->
                    when(it){
                        is BookUiState.Loading -> {
                            Log.d("TAG", it.id.toString())
                        }
                        is BookUiState.Success -> {
                            Log.d("TAG", it.book.id.toString())
                        }
                    }
                }
                LazyColumn {
                    items(
                        books,
                        key = {it ->
                            when(it){
                                is BookUiState.Loading -> {
                                    "L" + it.id
                                }
                                is BookUiState.Success -> {
                                    "S" + it.book.id
                                }
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
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                            .clickable { onItemClick(item.book.id) }) {
                                        Text(
                                            item.book.name.toString(), fontSize = 30.sp,
                                            textDecoration =
                                                if(item.book.id == curBookId) TextDecoration.Underline
                                                else TextDecoration.None
                                        )
                                        Text(
                                            "Страниц: " + item.book.pages.size.toString(), fontSize = 15.sp
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