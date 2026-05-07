package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable

@Composable
fun BAPageScreen(
    number: Int,
    text: String,
    onMove: (Int) -> Unit
){
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd ) {
                onMove(0)
                true
            } else {
                if (dismissValue == SwipeToDismissBoxValue.EndToStart){
                    onMove(1)
                    true
                }
                else
                    false
            }
        }
    )
    SwipeToDismissBox(
        dismissState,
        {}
    ) {
        Column() {
            Text(text)
            Text(number.toString())
        }
    }
}