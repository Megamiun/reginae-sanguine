package br.com.gabryel.reginaesanguine.server.service.security

private val jwt = js("require('jsonwebtoken')")

class JwtService(
    private val privateKey: String,
    private val publicKey: String,
) : TokenService {
    private val expiration = "30d"
    private val algorithm = "RS256"

    override fun generateToken(accountId: String): String {
        val payload = js("{}")
        payload.sub = accountId

        val options = js("{}")
        options.expiresIn = expiration
        options.algorithm = algorithm

        return jwt.sign(payload, privateKey, options) as String
    }

    override fun validateToken(token: String): String? = try {
        val options = js("{}")
        options.algorithms = arrayOf(algorithm)

        val decoded = jwt.verify(token, publicKey, options)
        decoded.sub as String
    } catch (e: dynamic) {
        null
    }
}
