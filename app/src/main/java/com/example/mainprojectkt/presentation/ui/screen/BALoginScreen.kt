package com.example.mainprojectkt.presentation.ui.screen

import android.util.Patterns
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.mainprojectkt.data.model.User
import org.mindrot.jbcrypt.BCrypt


@Composable
fun BALoginScreen(
    onBack: () -> Unit,
    onLogin: (String, String, (String) -> Unit) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    Scaffold(
        bottomBar = {
            Column() {
                Button({onBack()}) {
                    Icon(
                        Icons.Default.Home,
                        "На главную"
                    )
                }
            }
        }
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it},
                label = { Text("Почта") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it},
                label = { Text("Пароль") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Button({
                    if(email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Не все данные введены."
                    }
                    else{
                        onLogin(email, password){result ->
                            when (result) {
                                "Password" -> {
                                    errorMessage = "Пароль неверный."
                                }
                                "Email" -> {
                                    errorMessage = "Почта не найдена."
                                }
                                "Second" ->{
                                    errorMessage = "Вы уже в данном аккаунте."
                                }
                                else -> {
                                    onBack()
                                }
                            }
                        }
                    }
                }) {
                    Text("Войти")
                }
            }
            Text(errorMessage)
        }
    }
}