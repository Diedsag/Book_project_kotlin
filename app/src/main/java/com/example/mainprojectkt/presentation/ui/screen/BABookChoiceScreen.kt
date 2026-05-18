package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mainprojectkt.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BABookChoiceScreen(
    books: List<Book>,
    onItemClick: (Int) -> Unit
) {
    Scaffold(
    ) {padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                "Список книг",
                fontSize = 50.sp,
                modifier = Modifier.padding(bottom = 30.dp).testTag("X")
            )
            LazyColumn {
                items(
                    books,
                    key = {it.id}
                    ) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onItemClick(item.id) }) {
                            Text(
                                item.name.toString(), fontSize = 30.sp
                            )
                            Text(
                                "Страниц: " + item.pages.size.toString(), fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}