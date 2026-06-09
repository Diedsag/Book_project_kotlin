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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.mainprojectkt.R
import com.example.mainprojectkt.data.model.User
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

    val hintPasswordRequirements = stringResource(R.string.hint_password_requirements)
    val errorIncompleteData = stringResource(R.string.error_incomplete_data)
    val errorPasswordsMismatch = stringResource(R.string.error_passwords_mismatch)
    val errorPasswordNoLetters = stringResource(R.string.error_password_no_letters)
    val errorPasswordNoDigits = stringResource(R.string.error_password_no_digits)
    val errorPasswordNoSpecial = stringResource(R.string.error_password_no_special)
    val errorPasswordTooShort = stringResource(R.string.error_password_too_short)
    val errorInvalidEmail = stringResource(R.string.error_invalid_email)
    val errorEmailRegistered = stringResource(R.string.error_email_registered)
    val labelName = stringResource(R.string.label_name)
    val labelEmail = stringResource(R.string.label_email)
    val labelPassword = stringResource(R.string.label_password)
    val labelRepeatPassword = stringResource(R.string.label_repeat_password)
    val descHidePassword = stringResource(R.string.desc_hide_password)
    val descShowPassword = stringResource(R.string.desc_show_password)
    val navHome = stringResource(R.string.nav_home)
    val btnRegister = stringResource(R.string.btn_register)

    var errorMessage by remember { mutableStateOf(hintPasswordRequirements) }

    fun checkData() {
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || name.isEmpty()) {
            errorMessage = errorIncompleteData
        } else if (password != repeatPassword) {
            errorMessage = errorPasswordsMismatch
        } else if (!Regex("[A-Za-z]").containsMatchIn(password)) {
            errorMessage = errorPasswordNoLetters
        } else if (!Regex("\\d").containsMatchIn(password)) {
            errorMessage = errorPasswordNoDigits
        } else if (!Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").containsMatchIn(password)) {
            errorMessage = errorPasswordNoSpecial
        } else if (password.length < 8) {
            errorMessage = errorPasswordTooShort
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = errorInvalidEmail
        } else {
            errorMessage = ""
        }
    }

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
                value = name,
                onValueChange = {
                    name = it
                    checkData()
                },
                label = { Text(labelName) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    checkData()
                },
                label = { Text(labelEmail) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    checkData()
                },
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
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = {
                    repeatPassword = it
                    checkData()
                },
                label = { Text(labelRepeatPassword) },
                visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                        Icon(
                            if (repeatPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (repeatPasswordVisible) descHidePassword else descShowPassword
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
                    if (errorMessage == "") {
                        onAdd(User(0, email, name, BCrypt.hashpw(password, BCrypt.gensalt()))) { result ->
                            if (result)
                                onBack()
                            else
                                errorMessage = errorEmailRegistered
                        }
                    }
                }) {
                    Text(btnRegister)
                }
            }
            Text(errorMessage)
        }
    }
}