package com.example.ch3mxr.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemBrand
import com.example.ch3mxr.core.ui.components.ChemSocialButton
import com.example.ch3mxr.core.ui.components.ChemTextField
import com.example.ch3mxr.core.ui.theme.ChemDimens

@Composable
fun LoginScreen(
    onLoginClick: (username: String, password: String, rememberMe: Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ChemDimens.ScreenHorizontalPadding, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))
        ChemBrand()
        Spacer(Modifier.height(35.dp))

        ChemTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = "Usuario o Correo Electrónico",
            icon = Icons.Filled.Person,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(16.dp))
        ChemTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Contraseña",
            icon = Icons.Filled.Lock,
            isPassword = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onForgotPasswordClick) {
                Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                text = "Recordarme",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            )
        }

        Spacer(Modifier.height(20.dp))
        ChemButton(
            text = "INICIAR SESIÓN",
            onClick = { onLoginClick(username.trim(), password, rememberMe) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  O CONTINUAR CON  ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChemSocialButton(label = "Google", onClick = onGoogleClick)
            ChemSocialButton(label = "Facebook", onClick = onFacebookClick)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Regístrate", color = MaterialTheme.colorScheme.primary)
        }
    }
}
