package com.example.ch3mxr.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.example.ch3mxr.R
import com.example.ch3mxr.core.ui.components.ChemButton
import com.example.ch3mxr.core.ui.components.ChemBrand
import com.example.ch3mxr.core.ui.components.ChemSocialButton
import com.example.ch3mxr.core.ui.components.ChemTextField
import com.example.ch3mxr.core.ui.theme.ChemDimens

/** Vista sin logica de autenticacion: solo emite eventos para la capa futura. */
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Menos espacios en telefonos pequenos; scroll real si aun no cabe.
        val compact = maxHeight < 700.dp
        val verticalGap = if (compact) 8.dp else 15.dp
        val logoSize = ChemDimens.LogoSize * (if (compact) 0.73f else 1f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = ChemDimens.ScreenHorizontalPadding,
                    vertical = if (compact) 8.dp else 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChemBrand(logoSize = logoSize)
            Spacer(Modifier.height(if (compact) 8.dp else 20.dp))

            ChemTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Usuario",
                icon = Icons.Filled.Person,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(5.dp))
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
            Spacer(Modifier.height(verticalGap))
            ChemButton(
                text = "INICIAR SESIÓN",
                onClick = { onLoginClick(username.trim(), password, rememberMe) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(verticalGap))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChemSocialButton(label = "Google", iconRes = R.drawable.ic_google, onClick = onGoogleClick)
                ChemSocialButton(label = "Facebook", iconRes = R.drawable.ic_facebook, onClick = onFacebookClick)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onRegisterClick) {
                Text("¿No tienes cuenta? Regístrate", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
