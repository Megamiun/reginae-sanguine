package br.com.gabryel.reginaesanguine.viewmodel.auth

import br.com.gabryel.reginaesanguine.server.domain.AccountDto
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Authenticated
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Error
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.Loading
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthState.NotAuthenticated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object NotAuthenticated : AuthState

    data object Loading : AuthState

    data class Authenticated(val account: AccountDto, val token: String) : AuthState

    data class Error(val message: String) : AuthState
}

class AuthViewModel(
    private val authClient: AuthClient,
    private val storage: Storage,
    private val coroutineScope: CoroutineScope
) {
    private val stateFlow = MutableStateFlow<AuthState>(NotAuthenticated)
    val state = stateFlow.asStateFlow()

    init {
        restoreSession()
    }

    fun login(username: String, password: String) {
        coroutineScope.launch {
            stateFlow.value = Loading
            try {
                val request = LoginRequest(username, password)
                val response = authClient.login(request)

                storage.token.save(response.token)
                storage.accountId.save(response.account.id)

                stateFlow.value = Authenticated(response.account, response.token)
            } catch (e: Exception) {
                stateFlow.value = Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        coroutineScope.launch {
            stateFlow.value = Loading
            try {
                val request = CreateAccountRequest(username, email, password)
                val response = authClient.register(request)

                storage.token.save(response.token)
                storage.accountId.save(response.account.id)

                stateFlow.value = Authenticated(response.account, response.token)
            } catch (e: Exception) {
                stateFlow.value = Error(e.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        storage.token.clear()
        storage.accountId.clear()
        stateFlow.value = NotAuthenticated
    }

    private fun restoreSession() {
        val token = storage.token.retrieve()
        val accountId = storage.accountId.retrieve()

        if (token != null && accountId != null)
            stateFlow.value = Authenticated(AccountDto(id = accountId, username = "", email = ""), token)
    }
}
