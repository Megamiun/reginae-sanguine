package br.com.gabryel.reginaesanguine.server.service.security

import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCrypt as SpringBCrypt

@Service
class Bcrypt : PasswordHasher {
    override fun hash(password: String): String = SpringBCrypt.hashpw(password, SpringBCrypt.gensalt())

    override fun verify(password: String, hash: String): Boolean = SpringBCrypt.checkpw(password, hash)
}
