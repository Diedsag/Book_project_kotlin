package com.example.mainprojectkt.presentation.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mainprojectkt.presentation.ui.screen.BABookDetailScreen
import com.example.mainprojectkt.presentation.ui.screen.BABookListScreen
import com.example.mainprojectkt.presentation.ui.screen.BALoginScreen
import com.example.mainprojectkt.presentation.ui.screen.BAMainScreen
import com.example.mainprojectkt.presentation.ui.screen.BANoteScreen
import com.example.mainprojectkt.presentation.ui.screen.BAPageScreen
import com.example.mainprojectkt.presentation.ui.screen.BARegisterScreen
import com.example.mainprojectkt.presentation.ui.screen.BAScanScreen
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel
import com.example.mainprojectkt.presentation.viewmodel.BookUiState

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: BAViewModel) {
    val booksState by viewModel.booksUiState.collectAsStateWithLifecycle()
    val curBookId by viewModel.curBookId.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    val colorState by viewModel.colorState.collectAsStateWithLifecycle()
    val user by viewModel.userState.collectAsStateWithLifecycle()
    val themeState by viewModel.themeModeState.collectAsStateWithLifecycle()
    val bookNotesState = remember(curBookId, booksState, notesState) {
        viewModel.getBookNotes()
    }

    NavHost(navController = navController, startDestination = NavigationRoutes.Home.route) {
        composable(NavigationRoutes.Home.route) {
            BAMainScreen(
                toBooks = { navController.navigate(NavigationRoutes.Books.route) },
                toRegister = { navController.navigate(NavigationRoutes.Register.route) },
                toLogin = { navController.navigate(NavigationRoutes.Login.route) },
                onLogout = viewModel::logout,
                user = user,
                currentTheme = themeState,
                onThemeChange = viewModel::changeTheme
            )
        }

        composable(
            route = NavigationRoutes.Page.route,
            arguments = listOf(navArgument(NavigationRoutes.Page.ARG_NUMBER) { type = NavType.IntType })
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt(NavigationRoutes.Page.ARG_NUMBER) ?: return@composable
            booksState.find { it is BookUiState.Success && it.book.id == curBookId }.let { bookState ->
                val curBook = (bookState as? BookUiState.Success)?.book ?: return@let
                val page = curBook.pages[number - 1]
                BAPageScreen(
                    nPages = curBook.pages.size,
                    page = page,
                    curColor = colorState,
                    allNotes = notesState,
                    bookNotes = bookNotesState,
                    onMove = { num ->
                        navController.navigate(NavigationRoutes.Page.createRoute(num))
                        viewModel.changeLastNum(num)
                    },
                    onChangeStyle = viewModel::changePage,
                    onAddNote = viewModel::addNote,
                    onHome = { navController.navigate(NavigationRoutes.Home.route) },
                    onList = { navController.navigate(NavigationRoutes.Books.route) },
                    onDeleteNote = viewModel::deleteNote,
                    onChangeColor = viewModel::changeColor,
                    onBack = { navController.popBackStack() },
                    onTable = {
                        curBookId?.let { id ->
                            navController.navigate(NavigationRoutes.BookDetail.createRoute(id))
                        }
                    }
                )
            }
        }

        composable(
            route = NavigationRoutes.Note.route,
            arguments = listOf(navArgument(NavigationRoutes.Note.ARG_ID) { type = NavType.LongType }),
            deepLinks = listOf(navDeepLink { uriPattern = "myapp://note/{id}" })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong(NavigationRoutes.Note.ARG_ID) ?: return@composable
            val note = notesState.find { it.id == id } ?: return@composable
            val activity = LocalActivity.current
            BANoteScreen(
                note = note,
                onHome = { navController.navigate(NavigationRoutes.Home.route) },
                onBack = { activity?.finish() },
                onSave = viewModel::updateNote
            )
        }

        composable(NavigationRoutes.Pdf.route) {
            BAScanScreen(
                onScan = viewModel::scanBook,
                onBack = navController::popBackStack
            )
        }

        composable(NavigationRoutes.Books.route) {
            BABookListScreen(
                books = booksState,
                onSelect = { id ->
                    viewModel.curBookId.value = id
                    navController.navigate(NavigationRoutes.BookDetail.createRoute(id))
                },
                onResume = { id ->
                    viewModel.curBookId.value = id
                    booksState.find { it is BookUiState.Success && it.book.id == id }.let { bookState ->
                        val curBook = (bookState as? BookUiState.Success)?.book
                        curBook?.let { book ->
                            navController.navigate(NavigationRoutes.Page.createRoute(book.lastPage))
                        }
                    }
                },
                onBack = { navController.navigate(NavigationRoutes.Home.route) },
                onDelete = viewModel::deleteBook,
                onUpdate = viewModel::changeName,
                onAdd = { navController.navigate(NavigationRoutes.Pdf.route) }
            )
        }

        composable(
            route = NavigationRoutes.BookDetail.route,
            arguments = listOf(navArgument(NavigationRoutes.BookDetail.ARG_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong(NavigationRoutes.BookDetail.ARG_ID) ?: return@composable
            booksState.find { it is BookUiState.Success && it.book.id == id }.let { bookState ->
                val foundBook = (bookState as? BookUiState.Success)?.book ?: return@let
                BABookDetailScreen(
                    book = foundBook,
                    onMove = { num ->
                        navController.navigate(NavigationRoutes.Page.createRoute(num))
                        viewModel.changeLastNum(num)
                    },
                    onBack = { navController.navigate(NavigationRoutes.Home.route) },
                    onUpdate = viewModel::changeName
                )
            }
        }

        composable(NavigationRoutes.Register.route) {
            BARegisterScreen(
                onBack = { navController.navigate(NavigationRoutes.Home.route) },
                onAdd = viewModel::register
            )
        }

        composable(NavigationRoutes.Login.route) {
            BALoginScreen(
                onBack = { navController.navigate(NavigationRoutes.Home.route) },
                onLogin = viewModel::login
            )
        }
    }
}