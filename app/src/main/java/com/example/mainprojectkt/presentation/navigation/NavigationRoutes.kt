package com.example.mainprojectkt.presentation.navigation

sealed class NavigationRoutes(val route: String) {
    data object Home : NavigationRoutes("home")
    data object Books : NavigationRoutes("books")
    data object BookDetail : NavigationRoutes("book/{id}") {
        fun createRoute(id: Long) = "book/$id"
        const val ARG_ID = "id"
    }
    data object Page : NavigationRoutes("page/{number}") {
        fun createRoute(number: Int) = "page/$number"
        const val ARG_NUMBER = "number"
    }
    data object Note : NavigationRoutes("note/{id}") {
        const val ARG_ID = "id"
    }
    data object Pdf : NavigationRoutes("pdf")
    data object Register : NavigationRoutes("register")
    data object Login : NavigationRoutes("login")
}