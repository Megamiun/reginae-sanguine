package br.com.gabryel.reginaesanguine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.unit.dp
import br.com.gabryel.reginaesanguine.app.ui.theme.WhiteLight
import br.com.gabryel.reginaesanguine.app.util.Mode
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Authenticated
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Error
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Loading
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthViewModel

@Composable
fun ConfigurationDialog(
    currentMode: Mode,
    authViewModel: AuthViewModel,
    onConfigurationChanged: (Mode) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(if (currentMode == Mode.LOCAL) 0 else 1) }

    Box(
        Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        FancyBox(
            Modifier.width(400.dp).fillMaxHeight(0.8f).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        ) {
            header {
                Text("Configuration", color = WhiteLight)
            }

            body {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    verticalArrangement = spacedBy(8.dp),
                ) {
                    PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Local") },
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Remote") },
                        )
                    }

                    Column(Modifier.padding(16.dp)) {
                        when (selectedTabIndex) {
                            0 -> LocalConfigTab()
                            1 -> RemoteConfigTab(authViewModel = authViewModel)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(8.dp)) {
                            RButton("Cancel", modifier = Modifier.weight(1f)) {
                                onDismiss()
                            }
                            RButton(
                                "Apply",
                                modifier = Modifier.weight(1f),
                                enabled = selectedTabIndex == 0 || authViewModel.state.value is Authenticated,
                            ) {
                                val mode = if (selectedTabIndex == 0) Mode.LOCAL else Mode.REMOTE
                                onConfigurationChanged(mode)
                                onDismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocalConfigTab() {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = spacedBy(8.dp)) {
        Text("Local mode uses embedded game logic without server connection.", color = WhiteLight)
        Text("All game data is stored locally on your device.", color = WhiteLight)
    }
}

@Composable
private fun RemoteConfigTab(authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.state.collectAsState()

    Column(verticalArrangement = spacedBy(4.dp)) {
        Text("Authentication:", fontWeight = FontWeight.Medium, color = WhiteLight)

        when (authState) {
            is Authenticated -> {
                val account = (authState as Authenticated).account
                Text("Logged in as: ${account.username.ifEmpty { account.id }}", color = WhiteLight)
                RButton("Logout") { authViewModel.logout() }
            }

            else -> {
                var isRegisterMode by remember { mutableStateOf(false) }
                var email by remember { mutableStateOf("") }

                LabeledTextField(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    enabled = authState !is Loading,
                )

                if (isRegisterMode) {
                    LabeledTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        enabled = authState !is Loading,
                    )
                }

                LabeledTextField(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true,
                    enabled = authState !is Loading,
                )

                if (authState is Error)
                    Text((authState as Error).message, color = Color.Red)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = spacedBy(8.dp),
                    verticalAlignment = CenterVertically,
                ) {
                    if (isRegisterMode) {
                        RButton("Register", enabled = authState !is Loading) {
                            authViewModel.register(username, email, password)
                        }
                        RButton("Back to Login", enabled = authState !is Loading) {
                            isRegisterMode = false
                        }
                    } else {
                        RButton("Login", enabled = authState !is Loading) {
                            authViewModel.login(username, password)
                        }
                        RButton("Register", enabled = authState !is Loading) {
                            isRegisterMode = true
                        }
                    }

                    if (authState is Loading)
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), color = WhiteLight)
                }
            }
        }
    }
}

@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        label = { Text(text = label) },
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else None,
        singleLine = true,
    )
}
