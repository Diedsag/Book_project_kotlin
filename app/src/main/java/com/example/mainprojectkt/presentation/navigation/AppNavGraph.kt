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
import com.example.mainprojectkt.domain.model.Book
import com.example.mainprojectkt.presentation.ui.screen.BABookChoiceScreen
import com.example.mainprojectkt.presentation.ui.screen.BAMainScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPageScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPdfChoiceScreen
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: BAViewModel) {
    val booksState by viewModel.booksState.collectAsStateWithLifecycle()
    val curBook by viewModel.curBook.collectAsStateWithLifecycle()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            BAMainScreen(
                {number -> navController.navigate("page/${number}")},
                {navController.navigate("pdf")},
                {navController.navigate("books")},
                viewModel.hasBook.collectAsStateWithLifecycle().value,
            )
        }
        composable(
            "page/{number}",
            arguments = listOf(navArgument("number") {type = NavType.IntType})
            ) {backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number") ?: return@composable
            val page = booksState[curBook].pages[number - 1]
            BAPageScreen(
                booksState[curBook].pages.size,
                page,
                {num -> navController.navigate("page/${num}")
                    viewModel.changeLastNum(num)
                },
                viewModel::changePage,
                {navController.navigate("home")}
            )
        }
        composable("pdf") {
            BAPdfChoiceScreen(
                viewModel::changeUri,
                {navController.navigate("home")}
            )
        }
        composable("books") {
            BABookChoiceScreen(
                booksState,
                {navController.navigate("home")}
            )
        }
    }
}