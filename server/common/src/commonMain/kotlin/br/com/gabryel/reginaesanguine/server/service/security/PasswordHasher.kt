package br.com.gabryel.reginaesanguine.server.service.security

interface PasswordHasher {
    fun hash(password: String): String

    fun verify(password: String, hash: String): Boolean
}
