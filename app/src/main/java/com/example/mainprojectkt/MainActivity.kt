package com.example.mainprojectkt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mainprojectkt.data.local.BADataSource
import com.example.mainprojectkt.data.repository.BARepositoryImpl
import com.example.mainprojectkt.domain.repository.BARepository
import com.example.mainprojectkt.domain.usecase.GetPagesUseCase
import com.example.mainprojectkt.domain.usecase.GetTextUseCase
import com.example.mainprojectkt.presentation.navigation.AppNavGraph
import com.example.mainprojectkt.presentation.theme.MainProjectKtTheme
import com.example.mainprojectkt.presentation.viewmodel.BAViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        var navController: NavHostController
        val repository = BARepositoryImpl(
            BADataSource(this)
        )
        val getPagesUseCase = GetPagesUseCase(repository)

        val viewModel = BAViewModel(
            applicationContext,
            getPagesUseCase
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
