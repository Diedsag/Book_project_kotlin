package com.example.mainprojectkt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.data.repository.BARepositoryImpl
import com.example.mainprojectkt.domain.usecase.AddNoteUseCase
import com.example.mainprojectkt.domain.usecase.AddUserUseCase
import com.example.mainprojectkt.domain.usecase.DeleteBookUseCase
import com.example.mainprojectkt.domain.usecase.DeleteNoteUseCase
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetNotesUseCase
import com.example.mainprojectkt.domain.usecase.GetUserBooksUseCase
import com.example.mainprojectkt.domain.usecase.GetUserUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdateBookUseCase
import com.example.mainprojectkt.domain.usecase.UpdateNoteUseCase
import com.example.mainprojectkt.domain.usecase.UpdatePageUseCase
import com.example.mainprojectkt.presentation.navigation.AppNavGraph
import com.example.mainprojectkt.presentation.theme.AppTheme
import com.example.mainprojectkt.presentation.theme.ThemeMode
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        var navController: NavHostController
        val repository = BARepositoryImpl(
            BADataSource(this),
            BADatabase.getDatabase(this)
        )

        val scanBookUseCase = ScanBookUseCase(repository)
        val downloadBooksUseCase = DownloadBooksUseCase(repository)
        val updateBookUseCase = UpdateBookUseCase(repository)
        val updatePageUseCase = UpdatePageUseCase(repository)
        val getUserBooksUseCase = GetUserBooksUseCase(repository)
        val addNoteUseCase = AddNoteUseCase(repository)
        val getNotesUseCase = GetNotesUseCase(repository)
        val deleteNoteUseCase = DeleteNoteUseCase(repository)
        val addUserUseCase = AddUserUseCase(repository)
        val getUserUseCase = GetUserUseCase(repository)
        val deleteBookUseCase = DeleteBookUseCase(repository)
        val updateNoteUseCase = UpdateNoteUseCase(repository)

        val viewModel = BAViewModel(
            applicationContext,
            scanBookUseCase,
            downloadBooksUseCase,
            updateBookUseCase,
            updatePageUseCase,
            getUserBooksUseCase,
            addNoteUseCase,
            getNotesUseCase,
            deleteNoteUseCase,
            addUserUseCase,
            getUserUseCase,
            deleteBookUseCase,
            updateNoteUseCase
        )
        val initialTheme = viewModel.getInitialThemeSync()

        applyAppCompatTheme(initialTheme)

        setContent {
            navController = rememberNavController()
            val themeMode by viewModel.themeModeState.collectAsState(initial = initialTheme)
            val isSystemDark = isSystemInDarkTheme()

            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            AppTheme(
                darkTheme = isDark,
                dynamicColor = false
            ) {
                Scaffold { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AppNavGraph(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    private fun applyAppCompatTheme(mode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
    }
}
