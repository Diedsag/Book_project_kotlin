package com.example.mainprojectkt.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.mainprojectkt.R

@Composable
fun BALoginScreen(
    onBack: () -> Unit,
    onLogin: (String, String, (String) -> Unit) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val errorIncompleteData = stringResource(R.string.error_incomplete_data)
    val errorWrongPassword = stringResource(R.string.error_wrong_password)
    val errorEmailNotFound = stringResource(R.string.error_email_not_found)
    val errorAlreadyLoggedIn = stringResource(R.string.error_already_logged_in)
    val labelEmail = stringResource(R.string.label_email)
    val labelPassword = stringResource(R.string.label_password)
    val descHidePassword = stringResource(R.string.desc_hide_password)
    val descShowPassword = stringResource(R.string.desc_show_password)
    val navHome = stringResource(R.string.nav_home)
    val btnLogin = stringResource(R.string.btn_login)

    Scaffold(
        bottomBar = {
            Column {
                Button({ onBack() }) {
                    Icon(
                        Icons.Default.Home,
                        navHome
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(labelEmail) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(labelPassword) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) descHidePassword else descShowPassword
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button({
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = errorIncompleteData
                    } else {
                        onLogin(email, password) { result ->
                            when (result) {
                                "Password" -> {
                                    errorMessage = errorWrongPassword
                                }
                                "Email" -> {
                                    errorMessage = errorEmailNotFound
                                }
                                "Second" -> {
                                    errorMessage = errorAlreadyLoggedIn
                                }
                                else -> {
                                    onBack()
                                }
                            }
                        }
                    }
                }) {
                    Text(btnLogin)
                }
            }
            Text(errorMessage)
        }
    }
}