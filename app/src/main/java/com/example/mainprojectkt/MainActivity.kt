package com.example.mainprojectkt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.local.BADatabase
import com.example.mainprojectkt.data.repository.BARepositoryImpl
import com.example.mainprojectkt.domain.usecase.DownloadBooksUseCase
import com.example.mainprojectkt.domain.usecase.ScanBookUseCase
import com.example.mainprojectkt.domain.usecase.UploadBookUseCase
import com.example.mainprojectkt.presentation.navigation.AppNavGraph
import com.example.mainprojectkt.presentation.theme.MainProjectKtTheme
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
        val uploadBookUseCase = UploadBookUseCase(repository)
        val downloadBooksUseCase = DownloadBooksUseCase(repository)

        val viewModel = BAViewModel(
            applicationContext,
            scanBookUseCase,
            uploadBookUseCase,
            downloadBooksUseCase
        )
        setContent {
            navController = rememberNavController()
            MainProjectKtTheme {
                Scaffold() { paddingValues ->
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
}
