package br.com.gabryel.reginaesanguine.viewmodel.auth.remote

import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.client.post
import br.com.gabryel.reginaesanguine.server.domain.action.CreateAccountRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginRequest
import br.com.gabryel.reginaesanguine.server.domain.action.LoginResponse
import br.com.gabryel.reginaesanguine.viewmodel.auth.AuthClient

class RemoteAuthClient(private val client: ServerClient) : AuthClient {
    override suspend fun login(request: LoginRequest): LoginResponse =
        client.post("account/login", request)

    override suspend fun register(request: CreateAccountRequest): LoginResponse =
        client.post("account", request)
}
