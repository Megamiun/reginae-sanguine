package br.com.gabryel.reginaesanguine.server.service.security

private val bcryptJs = js("require('bcryptjs')")

class Bcrypt : PasswordHasher {
    override fun hash(password: String): String = bcryptJs.hashSync(password, 10) as String

    override fun verify(password: String, hash: String): Boolean = bcryptJs.compareSync(password, hash) as Boolean
}
