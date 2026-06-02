package com.example.mainprojectkt.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.presentation.ui.screen.BABookDetailScreen
import com.example.mainprojectkt.presentation.ui.screen.BABookListScreen
import com.example.mainprojectkt.presentation.ui.screen.BAMainScreen
import com.example.mainprojectkt.presentation.ui.screen.BANoteScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPageScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPdfChoiceScreen
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel
import com.example.mainprojectkt.presentation.viewmodel.BookUiState

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: BAViewModel) {
    val booksState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val curBookId by viewModel.curBookId.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            BAMainScreen(
                {navController.navigate("pdf")},
                {navController.navigate("books")},
            )
        }
        composable(
            "page/{number}",
            arguments = listOf(navArgument("number") {type = NavType.IntType})
            ) {backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number") ?: return@composable
            booksState.find { it is BookUiState.Success && it.book.id == curBookId }.let {
                val curBook = (it as BookUiState.Success).book
                val page = curBook.pages[number - 1]
                BAPageScreen(
                    curBook.pages.size,
                    page,
                    {num -> navController.navigate("page/${num}")
                        viewModel.changeLastNum(num)
                    },
                    viewModel::changePage,
                    viewModel::addNote,
                    {navController.navigate("home")}
                )
            }
        }
        composable(
            "note/{id}",
            arguments = listOf(navArgument("id") {type = NavType.LongType}),
            deepLinks = listOf(
                navDeepLink { uriPattern = "myapp://note/{id}" }
            )
        ) {backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            val note = notesState.find { it.id == id } ?: return@composable
            BANoteScreen(
                note.text,
                {navController.navigate("home")},
            )
        }
        composable("pdf") {
            BAPdfChoiceScreen(
                viewModel::scanBook,
                {navController.navigate("home")}
            )
        }
        composable("books") {
            BABookListScreen(
                booksState,
                {id -> viewModel.curBookId.value = id
                    viewModel.hasBook.value = true
                    navController.navigate("book/$id")
                },
                {id -> viewModel.curBookId.value = id
                    viewModel.hasBook.value = true
                    booksState.find { it is BookUiState.Success && it.book.id == id }.let {
                        val curBook = (it as BookUiState.Success).book
                        navController.navigate("page/${curBook.lastPage}")
                    }
                },
                {navController.navigate("home")},
                curBookId,
            )
        }
        composable(
            "book/{id}",
            arguments = listOf(navArgument("id") {type = NavType.LongType})
        ) {backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            booksState.find { it is BookUiState.Success && it.book.id == id }.let {
                val foundBook = (it as BookUiState.Success).book
                BABookDetailScreen(
                    foundBook,
                    {num -> navController.navigate("page/${num}")
                        viewModel.changeLastNum(num)
                    },
                    {navController.navigate("home")}
                )
            }
        }
    }
}