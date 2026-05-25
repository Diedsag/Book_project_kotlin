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
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val curBook = booksState.find { it is BookUiState.Success && it.book.id == curBookId }
            BAMainScreen(
                {request -> if(request.toIntOrNull() != null) navController.navigate("page/${request.toInt()}")},
                {navController.navigate("pdf")},
                {navController.navigate("books")},
                viewModel::getCount,
                {viewModel.uploadBook(curBook)},
                {viewModel.checkRelation(1)},
                viewModel.hasBook.collectAsStateWithLifecycle().value,
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
                    {navController.navigate("home")}
                )
            }

        }
        composable(
            "note/{number}",
            arguments = listOf(navArgument("number") {type = NavType.IntType}),
            deepLinks = listOf(
                navDeepLink { uriPattern = "myapp://note/{number}" }
            )
        ) {backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number") ?: return@composable
            BANoteScreen(
                "hehe $number",
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
                    viewModel.hasBook.value = true},
                {navController.navigate("home")},
                curBookId,
            )
        }
    }
}