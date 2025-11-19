package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.server.service.security.TokenService
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AuthHelper(private val tokenService: TokenService) {
    fun extractAccountId(authorization: String): String {
        val token = authorization.removePrefix("Bearer ").trim()
        return tokenService.validateToken(token)
            ?: throw ResponseStatusException(UNAUTHORIZED, "Invalid or expired token")
    }
}
