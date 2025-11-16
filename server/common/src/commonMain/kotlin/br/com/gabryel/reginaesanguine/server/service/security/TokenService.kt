package br.com.gabryel.reginaesanguine.server.service.security

interface TokenService {
    fun generateToken(accountId: String): String

    fun validateToken(token: String): String?
}
