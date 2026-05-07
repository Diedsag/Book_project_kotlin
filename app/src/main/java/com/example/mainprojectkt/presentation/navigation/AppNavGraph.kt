package com.example.mainprojectkt.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mainprojectkt.presentation.ui.screen.BAMainScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPageScreen
import com.example.mainprojectkt.presentation.ui.screen.DocumentViewer
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: BAViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            BAMainScreen(
                {number -> navController.navigate("page/$number")},
                {navController.navigate("pdf")}
            )
        }
        composable(
            "page/{number}",
            arguments = listOf(navArgument("number") {type = NavType.IntType})
            ) {backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number") ?: return@composable
            val text = viewModel.getText(number)
            BAPageScreen(number, text)
        }
        composable("pdf") {
            DocumentViewer(
                viewModel::changeUri,
                {navController.navigate("home")}
            )
        }
    }
}