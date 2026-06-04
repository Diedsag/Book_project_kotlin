package com.example.mainprojectkt.presentation.ui.screen

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mainprojectkt.data.model.User
import com.example.mainprojectkt.presentation.viewmodel.BookUiState
import org.mindrot.jbcrypt.BCrypt


@Composable
fun BARegisterScreen(
    onBack: () -> Unit,
    onAdd: (User, (Boolean) -> Unit) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
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
                value = name,
                onValueChange = { name = it},
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )
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
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it},
                label = { Text("Повторите пароль") },
                visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                        Icon(
                            if (repeatPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (repeatPasswordVisible) "Скрыть пароль" else "Показать пароль"
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
                    if (password.length < 8)
                        errorMessage = "Длина пароля меньше 8 символов."
                    else if (password != repeatPassword) {
                        errorMessage = "Пароли не совпадают."
                    }
                    else if(!Regex("[A-Za-z]").containsMatchIn(password)){
                        errorMessage = "Пароль не содержит английских букв."
                    }
                    else if(!Regex("\\d").containsMatchIn(password)){
                        errorMessage = "Пароль не содержит цифр."
                    }
                    else if(!Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").containsMatchIn(password)){
                        errorMessage = "Пароль не содержит специальных символов."
                    }
                    else if(email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || name.isEmpty()) {
                        errorMessage = "Не все данные введены."
                    }
                    else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        errorMessage = "Почта введена не корректна."
                    }
                    else {
                        onAdd(User(0, email, name, BCrypt.hashpw(password, BCrypt.gensalt()))){
                            result ->
                            if (result)
                                onBack()
                            else
                                errorMessage = "Почта уже зарегистрирована."
                        }
                    }
                }) {
                    Text("Зарегистрироваться")
                }
            }
            Text(errorMessage)
        }
    }
}