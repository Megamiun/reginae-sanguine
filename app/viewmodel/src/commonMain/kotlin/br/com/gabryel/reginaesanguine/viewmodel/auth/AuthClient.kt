package br.com.gabryel.reginaesanguine.viewmodel.auth

import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse

interface AuthClient {
    suspend fun login(request: LoginRequest): LoginResponse

    suspend fun register(request: CreateAccountRequest): LoginResponse
}
